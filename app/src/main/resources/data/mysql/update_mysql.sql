-- V4.2.20230224.10
DROP TABLE IF EXISTS `t_sys_role_data_organ`;
CREATE TABLE `t_sys_role_data_organ`  (
                                          `ORGAN_ID` varchar(36) COMMENT '机构ID',
                                          `ROLE_ID` varchar(36) COMMENT '角色ID',
                                          INDEX `ROLE_ID`(`ROLE_ID`) USING BTREE,
                                          INDEX `ORGAN_ID`(`ORGAN_ID`) USING BTREE
) ENGINE = InnoDB COMMENT = '角色数据权限机构范围表';

-- 4.1.20191115
ALTER TABLE `t_sys_serial_number`
ADD COLUMN `APP` varchar(36) COMMENT 'APP标识' ;

ALTER TABLE `t_sys_version_log`
ADD COLUMN `IS_SHELF` varchar(36) COMMENT 'APP标识' ;