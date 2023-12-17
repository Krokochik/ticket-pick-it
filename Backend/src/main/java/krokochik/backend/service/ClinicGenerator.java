package krokochik.backend.service;

import com.github.javafaker.Faker;
import krokochik.backend.model.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class ClinicGenerator {

    @Autowired
    ApplicationArguments args;

    private Queue<String> randomNames(int amount) {
        Queue<String> names = new LinkedList<>();
        Faker faker = new Faker();
        for (int i = 0; i < amount; i++) {
            names.add(faker.name().fullName());
        }
        return names;
    }

    private Queue<String> randomAddresses(int amount) {
        Queue<String> addresses = new LinkedList<>();
        Faker faker = new Faker();
        for (int i = 0; i < amount; i++) {
            addresses.add(faker.address()
                    .fullAddress().replaceAll(" ", "{SPACE}"));
        }

        return addresses;
    }

    private Speciality randomSpeciality() {
        val values = Speciality.values();
        return values[ThreadLocalRandom.current().nextInt(values.length)];
    }

    private WorkingHours randomSchedule() {
        final Time WORK_DAY_BEGIN = Time.valueOf("08:00:00"),
                WORK_DAY_END = Time.valueOf("19:00:00");
        final byte MIN_WORK_DAY = 2,
                MAX_WORK_DAY = 8;
        val schedule = new WorkingHours();
        int workDayCount = ThreadLocalRandom.current().nextInt(2, 6 + 1);
        for (int i = 0; i < workDayCount; i++) {
            val dayOfWeek = DayOfWeek.of(ThreadLocalRandom.current().nextInt(1, 6 + 1));
            if (schedule.get().containsKey(dayOfWeek)) {
                i--;
                continue;
            }
            int workDay = ThreadLocalRandom.current().nextInt(MIN_WORK_DAY, MAX_WORK_DAY + 1);
            int periodCount = ThreadLocalRandom.current().nextInt(1, Math.round(Math.max(1, workDay / 2.5f)) + 1);
            val dayBegin = Time.valueOf("00:00:00");
            dayBegin.setHours(WORK_DAY_BEGIN.getHours() + Math.round(ThreadLocalRandom.current().nextFloat(0,
                    WORK_DAY_END.getHours() - WORK_DAY_BEGIN.getHours() - workDay + 2)));
            int breakMinutes = new int[]{0, 5, 10, 15}[ThreadLocalRandom.current().nextInt(
                    periodCount == 1 ? 0 : 1, (periodCount == 1 ? 0 : 3) + 1)];
            Time prevEnd = null;
            for (int j = 0; j < periodCount; j++) {
                if (prevEnd == null) prevEnd = dayBegin;
                val periodEnd = Time.valueOf((prevEnd.getHours() + workDay / periodCount + ":" + Math.round(
                        prevEnd.getMinutes() + (float) workDay / periodCount % 1 * 60)) + ":00");
                val p = new WorkingHours.Period(prevEnd, periodEnd);
                schedule.addPeriod(dayOfWeek, p);
                prevEnd = new Time(periodEnd.getTime() + TimeUnit.MINUTES.toMillis(breakMinutes));
            }
        }
        return schedule;
    }

    private Queue<String> randomClinicNames(int amount) {
        Queue<String> result = new LinkedList<>();
        Faker faker = new Faker();
        for (int i = 0; i < amount; i++) {
            result.add(faker.medical()
                    .hospitalName().replaceAll(" ", "{SPACE}"));
        }

        return result;
    }

    @SneakyThrows
    public List<Clinic> generateClinics(int clinicAmount) {
        LocalDate now = LocalDate.now();
        List<Clinic> clinics = new ArrayList<>();
        Queue<String> names = randomNames(clinicAmount * 30);
        Queue<String> addresses = randomAddresses(clinicAmount);
        Queue<String> clinicNames = randomClinicNames(clinicAmount);

        int days = YearMonth.now().lengthOfMonth() - now.getDayOfMonth() + 1 +
                YearMonth.now().plusMonths(1).lengthOfMonth();

        log.info(String.format("Start compiling %d clinics", clinicAmount));
        ProgressBarBuilder pbb = new ProgressBarBuilder()
                .setStyle(ProgressBarStyle.ASCII)
                .setUpdateIntervalMillis(10)
                .setUnit(" steps", 1)
                .setTaskName("Compiling")
                .setInitialMax(clinicAmount * 3L + days);
        ProgressBar pb = pbb.build();

        ExecutorService executorService;
        val async = (args.containsOption("async") &&
                !args.getOptionValues("async").contains("off")) ||
                args.getNonOptionArgs().contains("-async");
        if (async) {
            executorService = Executors.newWorkStealingPool();
        } else executorService = Executors.newSingleThreadExecutor();

        for (var counter = new Object() {int i = 0;}; counter.i < clinicAmount; counter.i++) {
            executorService.submit(() -> {
                Clinic clinic = new Clinic();
                clinic.setName(clinicNames.poll());
                val medicCount = ThreadLocalRandom.current().nextInt(8, 30 + 1);
                pb.step();

                for (int j = 0; j < medicCount; j++) {
                    val name = Objects.requireNonNull(names.poll()).split(" ");
                    Speciality[] medicSpecialities = new Speciality[1];
                    val r = ThreadLocalRandom.current().nextInt(0, 100 + 1);
                    if (r < 10) {
                        medicSpecialities = new Speciality[3];
                    } else if (r < 30) {
                        medicSpecialities = new Speciality[2];
                    }

                    for (int k = 0; k < medicSpecialities.length; k++) {
                        medicSpecialities[k] = randomSpeciality();
                    }
                    val medic = new Medic(
                            Arrays.stream(medicSpecialities).toList(),
                            name[0], name[1], String
                            .format("https://randomuser.me/api/portraits/%s/%d.jpg",
                                    ThreadLocalRandom.current().nextInt(0, 2) == 0
                                            ? "women" : "men",
                                    ThreadLocalRandom.current().nextInt(0, 100)));
                    for (Speciality medicSpeciality : medicSpecialities) {
                        clinic.getEmployees().add(new Employee(medic, new Specialist(
                                medicSpeciality,
                                (short) ThreadLocalRandom.current().nextInt(100, 725 + 1),
                                (short) ThreadLocalRandom.current().nextInt(6, 25 + 1),
                                randomSchedule(),
                                null,
                                null
                        )));
                    }
                }
                pb.step();

                val date = now.plusDays(counter.i);
                val workingTime = new AtomicLong();
                clinic.getEmployees().forEach(e -> {
                    if (e.getSpecialist().getWorkingHours().get().get(date.getDayOfWeek()) != null) {
                        workingTime.set(workingTime.get() +
                                e.getSpecialist().getWorkingHours().sum(date.getDayOfWeek()));
                    }
                });
                clinic.setAddress(addresses.poll());
                clinic.setPhone(new Faker(new Locale("en-US")).phoneNumber().phoneNumber());
                clinics.add(clinic);
                pb.step();
            });
        }

        executorService.shutdown();
        if (!executorService.awaitTermination(clinicAmount * 3L, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
        }

        // fulfilling available tickets list
        for (int i = 0; i < days; i++) {
            LocalDate date = now.plusDays(i);
            clinics.forEach(c -> c.getEmployees().forEach(e -> {
                val sp = e.getSpecialist();
                List<Ticket> tickets = new ArrayList<>();
                if (sp.getTickets() != null) tickets.addAll(sp.getTickets());
                if (sp.getWorkingHours().get().get(date.getDayOfWeek()) != null) {
                    sp.getWorkingHours().get().get(date.getDayOfWeek())
                            .forEach(period -> {
                                int ticketCount = (int) Math.ceil((float)
                                        period.length(TimeUnit.MINUTES) /
                                        sp.getMinutesPerPatient());
                                for (int j = 0; j < ticketCount; j++) {
                                    LocalDateTime ticketDate = date
                                            .atTime(period.begin().toLocalTime())
                                            .plusMinutes(j * (long) sp.getMinutesPerPatient());
                                    tickets.add(new Ticket(ticketDate, false));
                                }
                            });
                }
                sp.setTickets(tickets);
            }));
        }
        if (clinicAmount == clinics.size()) {
            pb.stepTo(pb.getMax());
            pb.close();
            log.info("Random clinic list generated");
        } else {
            pb.close();
            log.error(String.format("Unknown error during generating clinics (%d / %d generated)",
                    pb.getCurrent(), pb.getMax()));
        }
        return clinics;
    }

    public List<Clinic> generateClinics() {
        if (args.containsOption("clinics-amount"))
            return generateClinics(Math.min(500, (int) Math.round(Double.parseDouble(
                    args.getOptionValues("clinics-amount").get(0)))));
        return generateClinics(ThreadLocalRandom.current().nextInt(100, 500));
    }
}
