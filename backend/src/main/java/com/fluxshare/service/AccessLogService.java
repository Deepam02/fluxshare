package com.fluxshare.service;

import com.fluxshare.entity.AccessLog;
import com.fluxshare.entity.Share;
import com.fluxshare.enums.AccessAction;
import com.fluxshare.repository.AccessLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing access logs
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AccessLogService {

    private final AccessLogRepository accessLogRepository;

    @Value("${fluxshare.access-log.enabled:true}")
    private boolean accessLogEnabled;

    /**
     * Log an access attempt
     */
    @Transactional
    public void logAccess(Share share, AccessAction action, HttpServletRequest request, 
                         String fileName, boolean success, String errorMessage) {
        if (!accessLogEnabled) {
            return;
        }

        try {
            AccessLog log = AccessLog.builder()
                    .share(share)
                    .action(action.name())
                    .ipAddress(getClientIp(request))
                    .userAgent(request.getHeader("User-Agent"))
                    .fileName(fileName)
                    .success(success)
                    .errorMessage(errorMessage)
                    .build();

            accessLogRepository.save(log);
            AccessLogService.log.debug("Access logged: {} for share {} by IP {}", 
                    action, share.getShareId(), log.getIpAddress());
        } catch (Exception e) {
            AccessLogService.log.error("Failed to log access", e);
        }
    }

    /**
     * Log a successful access
     */
    @Transactional
    public void logAccess(Share share, AccessAction action, HttpServletRequest request) {
        logAccess(share, action, request, null, true, null);
    }

    /**
     * Get access logs for a share
     */
    public List<AccessLog> getAccessLogs(Share share) {
        return accessLogRepository.findByShareOrderByAccessTimestampDesc(share);
    }

    /**
     * Get recent access logs for a share
     */
    public List<AccessLog> getRecentAccessLogs(Share share, int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return accessLogRepository.findRecentAccessLogs(share.getId(), since);
    }

    /**
     * Count access attempts from an IP for rate limiting
     */
    public long countAccessFromIp(Share share, String ipAddress, int minutes) {
        LocalDateTime since = LocalDateTime.now().minusMinutes(minutes);
        return accessLogRepository.countByShareIdAndIpAddressSince(
                share.getId(), ipAddress, since);
    }

    /**
     * Extract client IP address from request
     */
    private String getClientIp(HttpServletRequest request) {
        String[] headers = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // Get first IP if multiple
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }
}
