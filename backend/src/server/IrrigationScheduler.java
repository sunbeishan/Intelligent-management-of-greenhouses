package server;

import dao.ActivityDao;
import dao.IrrigationPlanDAO;
import dao.IrrigationRecordDAO;
import model.IrrigationPlan;
import model.IrrigationRecord;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class IrrigationScheduler {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final IrrigationPlanDAO planDAO = new IrrigationPlanDAO();
    private static final IrrigationRecordDAO recordDAO = new IrrigationRecordDAO();
    private static final Map<Integer, Long> lastExecution = new HashMap<>();

    public static void start() {
        scheduler.scheduleAtFixedRate(IrrigationScheduler::checkAndExecute, 0, 1, TimeUnit.MINUTES);
        System.out.println("灌溉定时调度器已启动");
    }

    public static void stop() {
        scheduler.shutdown();
    }

    private static void checkAndExecute() {
        try {
            List<IrrigationPlan> activePlans = planDAO.getActivePlans();
            LocalDateTime now = LocalDateTime.now();
            int currentDayOfWeek = now.getDayOfWeek().getValue();
            LocalTime currentTime = LocalTime.now();

            for (IrrigationPlan plan : activePlans) {
                if (shouldExecute(plan, currentDayOfWeek, currentTime)) {
                    executePlan(plan, now);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean shouldExecute(IrrigationPlan plan, int currentDayOfWeek, LocalTime currentTime) {
        try {
            Time startTime = plan.getStartTime();
            if (startTime == null) return false;

            LocalTime planTime = startTime.toLocalTime();
            
            if (Math.abs(planTime.getHour() - currentTime.getHour()) > 0 || 
                Math.abs(planTime.getMinute() - currentTime.getMinute()) > 1) {
                return false;
            }

            Long lastExec = lastExecution.get(plan.getId());
            long now = System.currentTimeMillis();
            if (lastExec != null && (now - lastExec) < 60000) {
                return false;
            }

            String frequency = plan.getFrequency();
            String weekDays = plan.getWeekDays();

            if ("每天".equals(frequency)) {
                return true;
            } else if ("每周".equals(frequency) || "自定义".equals(frequency)) {
                if (weekDays != null && !weekDays.isEmpty()) {
                    String[] days = weekDays.split(",");
                    for (String day : days) {
                        try {
                            int d = Integer.parseInt(day.trim());
                            if (d == currentDayOfWeek) {
                                return true;
                            }
                        } catch (NumberFormatException e) {
                        }
                    }
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private static void executePlan(IrrigationPlan plan, LocalDateTime now) {
        try {
            System.out.println("执行灌溉计划: " + plan.getPlanName());

            Timestamp startTime = Timestamp.valueOf(now);
            Timestamp endTime = Timestamp.valueOf(now.plusMinutes(plan.getDuration()));

            IrrigationRecord record = new IrrigationRecord();
            record.setFarmlandId(plan.getFarmlandId());
            record.setDeviceId(plan.getDeviceId());
            record.setPlanId(plan.getId());
            record.setStartTime(startTime);
            record.setEndTime(endTime);
            record.setDuration(plan.getDuration());
            record.setWaterAmount(plan.getWaterAmount());
            record.setType("自动");
            record.setStatus("已完成");
            record.setOperator("系统");
            record.setRemark("自动灌溉计划: " + plan.getPlanName());

            if (recordDAO.addRecord(record)) {
                System.out.println("灌溉记录创建成功: " + plan.getPlanName());
                lastExecution.put(plan.getId(), System.currentTimeMillis());
                ActivityDao.recordActivity("农田管理", "自动灌溉执行完成: " + plan.getPlanName());
            } else {
                System.out.println("灌溉记录创建失败: " + plan.getPlanName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
