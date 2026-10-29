package com.zalo.modules.schedule.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ScheduleService {

    // 1. Chạy theo khoảng thời gian cố định tính từ thời điểm KẾT THÚC lần chạy trước (miliseconds)
    @Scheduled(fixedDelay = 5000)
    public void scheduleTaskUsingFixedDelay() {
        System.out.println("Fixed Delay Task - " + new Date());
    }

    // 2. Chạy theo khoảng thời gian cố định tính từ thời điểm BẮT ĐẦU lần chạy trước
    @Scheduled(fixedRate = 3000)
    public void scheduleTaskUsingFixedRate() {
        System.out.println("Fixed Rate Task - " + new Date());
    }

    // 3. Chạy lần đầu sau 5 giây kể từ khi ứng dụng khởi động, sau đó chạy đều đặn mỗi 2 giây
    @Scheduled(initialDelay = 5000, fixedRate = 2000)
    public void scheduleTaskWithInitialDelay() {
        System.out.println("Initial Delay Task - " + new Date());
    }

    // 4. Chạy theo biểu thức Cron Expression (Linh hoạt nhất)
    // Ví dụ: Chạy vào giây thứ 0 của mỗi phút
    @Scheduled(cron = "0 * * * * *")
    public void scheduleTaskUsingCronExpression() {
        System.out.println("Cron Expression Task - " + new Date());
    }
}
