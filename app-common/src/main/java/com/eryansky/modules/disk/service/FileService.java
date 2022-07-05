/**
 * Copyright (c) 2012-2022 https://www.eryansky.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.disk.service;

import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.model.Parameter;
import com.eryansky.common.orm.mybatis.interceptor.BaseInterceptor;
import com.eryansky.common.utils.DateUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.io.FileUtils;
import com.eryansky.core.orm.mybatis.entity.DataEntity;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.core.web.upload.FileUploadUtils;
import com.eryansky.modules.disk._enum.FileSizeType;
import com.eryansky.modules.disk.extend.IFileManager;
import com.eryansky.modules.disk.mapper.Folder;
import com.eryansky.modules.disk.utils.DiskUtils;
import com.eryansky.utils.AppConstants;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.eryansky.modules.disk.mapper.File;
import com.eryansky.modules.disk.dao.FileDao;
import com.eryansky.core.orm.mybatis.service.CrudService;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 *  service
 * @author eryan
 * @date 2018-05-04
 */
@Service
public class FileService extends CrudService<FileDao, File> {

    @Autowired
    @Lazy
    private FolderService folderService;
    @Autowired
    private IFileManager iFileManager;

    /**
     * 根据ID查找
     * @param fileIds
     * @return
     */
    public List<File> findFilesByIds(Collection<String> fileIds) {
        if (Collections3.isEmpty(fileIds)) {
            return Collections.emptyList();
        }
        Parameter parameter = Parameter.newParameter();
        parameter.put(DataEntity.FIELD_STATUS, DataEntity.STATUS_NORMAL);
        parameter.put("fileIds", fileIds);
        return dao.findFilesByIds(parameter);
    }

    @Override
    public Page<File> findPage(Page<File> page, File entity) {
        return super.findPage(page, entity);
    }


    /**
     * 文件检索
     *
     * @param userId 是否指定权限人员
     * @param query 文件名称
     * @param folderAuthorize  云盘类型
     * @param fileSizeType 文件大小类型
     * @param startTime 上传时间启
     * @param endTime 上传时间止
     * @return
     */

    public Page<File> searchFilePage(Page<File> page, String userId,
                                     String query, String folderAuthorize, String fileSizeType,
                                     boolean isAdmin,
                                     Date startTime, Date endTime) {
        Parameter patameter = new Parameter();
        patameter.put(BaseInterceptor.PAGE, page);
        patameter.put(DataEntity.FIELD_STATUS, DataEntity.STATUS_NORMAL);
        patameter.put(BaseInterceptor.DB_NAME, AppConstants.getJdbcType());

        patameter.put("folderAuthorize", folderAuthorize);
        patameter.put("isAdmin", isAdmin);
        patameter.put("userId", userId);
        patameter.put("query", query);
        patameter.put("fileSizeType", fileSizeType);
        patameter.put("startTime", startTime == null ? null : DateUtils.formatDateTime(startTime));
        patameter.put("endTime", endTime == null ? null : DateUtils.formatDateTime(endTime));
        if (fileSizeType != null) {
            Long minSize = 10 * 1024 * 1024L;
            Long maxSize = 100 * 1024 * 1024L;
            if (FileSizeType.MIN.getValue().equals(fileSizeType)) {
                patameter.put("fileSize", minSize);
            } else if (FileSizeType.MIDDEN.getValue().equals(fileSizeType)) {
                patameter.put("minSize", minSize);
                patameter.put("maxSize", maxSize);
            } else if (FileSizeType.MAX.getValue().equals(fileSizeType)) {
                patameter.put("fileSize", maxSize);
            }
        }
        return page.setResult(dao.findAdvenceQueryList(patameter));
    }


    /**
     * 保存系统文件
     * @param folderCode
     * @param file
     * @return
     */
    public File saveSystemFile(String folderCode, File file) {
        Validate.notBlank(folderCode, "参数[folderCode]不能为null.");
        Validate.notNull(file, "参数[file]不能为null.");
        Folder folder = folderService.checkAndSaveSystemFolderByCode(folderCode);
        file.setFolderId(folder.getId());
        save(file);
        return file;
    }


    /**
     * 文件上传
     * @param sessionInfo
     * @param folder
     * @param uploadFile
     * @throws Exception
     */
    public File fileUpload(SessionInfo sessionInfo, Folder folder,
                           MultipartFile uploadFile) {
        File file = null;
        /*		Exception exception = null;
         */
        java.io.File tempFile = null;
        String fullName = DiskUtils.getMultipartOriginalFilename(uploadFile);
        String code = FileUploadUtils.encodingFilenamePrefix(sessionInfo.getUserId(), fullName);
        String storePath = iFileManager.getStorePath(folder, sessionInfo.getUserId(), fullName);

        String fileTemp = AppConstants.getDiskTempDir() + java.io.File.separator + code;
        tempFile = new java.io.File(fileTemp);
        try (FileOutputStream fos = FileUtils.openOutputStream(tempFile)){
            IOUtils.copy(uploadFile.getInputStream(), fos);
            iFileManager.saveFile(storePath, fileTemp, false);
            file = new File();
            file.setFolderId(folder.getId());
            file.setCode(code);
            file.setUserId(sessionInfo.getUserId());
            file.setName(fullName);
            file.setFilePath(storePath);
            file.setFileSize(uploadFile.getSize());
            file.setFileSuffix(FilenameUtils.getExtension(fullName));
            save(file);
        } catch (Exception e) {
            // exception = e;
            throw new ServiceException(DiskUtils.UPLOAD_FAIL_MSG + e.getMessage(), e);
        } finally {
//			if (exception != null && file != null) {
//				DiskUtils.deleteByFileId(null, file.getId());
//			}
            if (tempFile != null && tempFile.exists()) {
                boolean deleteFlag = tempFile.delete();
                logger.warn("temp file delete {}:{}",tempFile.getName(),deleteFlag);
            }

        }
        return file;

    }

    /**
     *
     * 文件删除
     * 删除磁盘文件
     * @param fileId 文件ID
     */
    public void deleteByFileId(String fileId) {
        deleteByFileId(fileId, false);
    }

    /**
     *
     * 文件删除
     * @param fileId 文件ID
     * @param deleteDiskFile 删除磁盘文件
     */
    public void deleteByFileId(String fileId, boolean deleteDiskFile) {
        File file = dao.get(fileId);
        try {
            //检查文件是否被引用
            List<File> files = this.findByCode(file.getCode(), fileId);
            if (deleteDiskFile && Collections3.isEmpty(files)) {
                iFileManager.deleteFile(file.getFilePath());
                logger.debug("删除文件：{}", new Object[]{file.getFilePath()});
            }
            dao.delete(file);
        } catch (IOException e) {
            logger.error("删除文件[{}]失败,{}", new Object[]{file.getFilePath(), e.getMessage()});
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 根据文件夹ID级联删除文件（包含下级文件夹）
     * @param fileId 文件ID
     * @return
     */
    public void deleteCascadeByFolderId(String fileId) {
        deleteCascadeByFolderId(fileId, false);
    }

    /**
     * 根据文件夹ID级联删除文件（包含下级文件夹）
     * @param fileId 文件ID
     * @param deleteDiskFile 删除磁盘文件
     * @return
     */
    public void deleteCascadeByFolderId(String fileId, boolean deleteDiskFile) {
        File file = dao.get(fileId);
        try {
            //检查文件是否被引用
            List<File> files = this.findByCode(file.getCode(), fileId);
            if (deleteDiskFile && Collections3.isEmpty(files)) {
                iFileManager.deleteFile(file.getFilePath());
                logger.debug("删除文件：{}", new Object[]{file.getFilePath()});
            }
            dao.deleteCascadeByFolderId(file);
        } catch (IOException e) {
            logger.error("删除文件[{}]失败,{}", new Object[]{file.getFilePath(), e.getMessage()});
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }


    /**
     *
     * 文件删除
     * @param fileIds 文件集合
     */
    public void deleteFileByFileIds(Collection<String> fileIds) {
        if (Collections3.isNotEmpty(fileIds)) {
            for (String fileId : fileIds) {
                deleteByFileId(fileId);
            }
        } else {
            logger.warn("参数[ids]为空.");
        }
    }


    /**
     *
     * 文件级联删除
     *
     * @param fileCodes 文件编码集合
     */
    public void deleteFileByFolderCode(Collection<String> fileCodes) {
        if (Collections3.isNotEmpty(fileCodes)) {
            for (String code : fileCodes) {
                List<File> fileList = findByCode(code, null);
                if (Collections3.isNotEmpty(fileList)) {
                    for (File file : fileList) {
                        deleteByFileId(file.getId(), true);
                    }
                }
            }
        } else {
            logger.warn("参数[ids]为空.");
        }

    }


    /**
     * 查找文件夹下所有文件
     * @param folderId 文件夹ID
     * @return
     */
    public List<File> findFolderFiles(String folderId) {
        return findFolderFiles(folderId, null);
    }

    /**
     * 查找文件夹下所有文件
     * @param folderId 文件夹ID
     * @param fileSuffixs 文件后缀名
     * @return
     */
    public List<File> findFolderFiles(String folderId, Collection<String> fileSuffixs) {
        Parameter parameter = Parameter.newParameter();
        parameter.put(DataEntity.FIELD_STATUS, DataEntity.STATUS_NORMAL);
        parameter.put(BaseInterceptor.DB_NAME, AppConstants.getJdbcType());
        parameter.put("folderId", folderId);
        parameter.put("fileSuffixs", fileSuffixs);
        return dao.findFolderFiles(parameter);
    }

    public List<File> findOwnerAndChildsFolderFiles(String folderId) {
        Parameter parameter = Parameter.newParameter();
        parameter.put(DataEntity.FIELD_STATUS, DataEntity.STATUS_NORMAL);
        parameter.put(BaseInterceptor.DB_NAME, AppConstants.getJdbcType());
        parameter.put("folderId", folderId);
        return dao.findOwnerAndChildsFolderFiles(parameter);
    }

    public List<String> findOwnerAndChildsIdsFolderFiles(String folderId) {
        Parameter parameter = Parameter.newParameter();
        parameter.put(DataEntity.FIELD_STATUS, DataEntity.STATUS_NORMAL);
        parameter.put(BaseInterceptor.DB_NAME, AppConstants.getJdbcType());
        parameter.put("folderId", folderId);
        return dao.findOwnerAndChildsIdsFolderFiles(parameter);
    }

    /**
     * 根据文件标识获取文件
     * @param code 文件标识
     * @param excludeFileId 排除的文件ID  可为null
     * @return
     */
    private List<File> findByCode(String code, String excludeFileId) {
        Parameter parameter = Parameter.newParameter();
        parameter.put(DataEntity.FIELD_STATUS, DataEntity.STATUS_NORMAL);
        parameter.put("code", code);
        parameter.put("excludeFileId", excludeFileId);
        return dao.findByCode(parameter);
    }


    /**
     * 统计文件大小
     * @param fileIds 文件ID集合
     * @return
     */
    public Long countFileSize(Collection<String> fileIds) {
        Parameter parameter = Parameter.newParameter();
        parameter.put(DataEntity.FIELD_STATUS, DataEntity.STATUS_NORMAL);
        parameter.put("fileIds", fileIds);
        return dao.countFileSize(parameter);
    }

}
