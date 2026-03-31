package com.fpm.service;

import com.fpm.dto.ApprovalStatusUpdateDTO;
import com.fpm.dto.UploadProgressUpdateDTO;

public interface RealTimeStatusService {

    // STORY: FPMAPP-8913 - Interface method to send approval status updates
    void sendApprovalStatusUpdate(ApprovalStatusUpdateDTO update);

    // STORY: FPMAPP-8913 - Interface method to send upload progress updates
    void sendUploadProgressUpdate(UploadProgressUpdateDTO update);
}