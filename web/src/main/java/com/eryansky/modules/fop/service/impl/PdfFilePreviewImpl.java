package com.eryansky.modules.fop.service.impl;

import com.eryansky.modules.fop.service.FilePreview;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Created by kl on 2018/1/17.
 * Content :处理pdf文件
 */
@Service
public class PdfFilePreviewImpl implements FilePreview {

    @Override
    public String filePreviewHandle(String url, Model model) {
        String pdfUrl = StringUtils.startsWithIgnoreCase(url,"http:") || StringUtils.startsWithIgnoreCase(url,"https:") ? url:
                ((String) RequestContextHolder.currentRequestAttributes().getAttribute("baseUrl", 0))+url;
        model.addAttribute("pdfUrl", pdfUrl);
        return "pdf";
    }
}
