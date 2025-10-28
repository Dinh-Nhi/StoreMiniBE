package com.storemini.service.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VnpayService {

    @Value("${vnpay.tmn-code}")
    private String vnpTmnCode;

    @Value("${vnpay.hash-secret}")
    private String vnpHashSecret;

    @Value("${vnpay.return-url}")
    private String vnpReturnUrl;

    @Value("${vnpay.pay-url}")
    private String vnpUrl;

    public String createPaymentUrl(Long orderId, long amount) {
        try {
            Map<String, String> vnpParams = new HashMap<>();
            vnpParams.put("vnp_Version", "2.1.0");
            vnpParams.put("vnp_Command", "pay");
            vnpParams.put("vnp_TmnCode", vnpTmnCode);
            vnpParams.put("vnp_Amount", String.valueOf(amount * 100)); // nhân 100
            vnpParams.put("vnp_CurrCode", "VND");
            vnpParams.put("vnp_TxnRef", String.valueOf(orderId));
            vnpParams.put("vnp_OrderInfo", "Thanh toan don hang #" + orderId);
            vnpParams.put("vnp_OrderType", "other");
            vnpParams.put("vnp_Locale", "vn");
            vnpParams.put("vnp_ReturnUrl", vnpReturnUrl);
            vnpParams.put("vnp_IpAddr", "127.0.0.1");
            vnpParams.put("vnp_CreateDate",
                    new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

            // ✅ Sắp xếp key theo thứ tự alphabet
            List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
            Collections.sort(fieldNames);

            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            for (int i = 0; i < fieldNames.size(); i++) {
                String fieldName = fieldNames.get(i);
                String fieldValue = vnpParams.get(fieldName);
                if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                    hashData.append(fieldName).append('=')
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    query.append(fieldName).append('=')
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    if (i < fieldNames.size() - 1) {
                        hashData.append('&');
                        query.append('&');
                    }
                }
            }

            String vnp_SecureHash = hmacSHA512(vnpHashSecret, hashData.toString());
            query.append("&vnp_SecureHash=").append(vnp_SecureHash);

            return vnpUrl + "?" + query;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo URL thanh toán VNPAY", e);
        }
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] bytes = hmac512.doFinal(data.getBytes());
            StringBuilder hash = new StringBuilder();
            for (byte b : bytes) {
                hash.append(String.format("%02x", b));
            }
            return hash.toString();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi mã hóa HmacSHA512", e);
        }
    }
}
