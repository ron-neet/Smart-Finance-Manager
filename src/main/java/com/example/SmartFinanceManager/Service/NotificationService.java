package com.example.SmartFinanceManager.Service;

import com.example.SmartFinanceManager.Dto.ExpenseDto;
import com.example.SmartFinanceManager.Model.Profile;
import com.example.SmartFinanceManager.Repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final ExpenseService expenseService;

    @Value("${money.manager.frontend.url}")
    private String frontendUrl;

    @Scheduled(cron = "0 0 22 * * *", zone = "GMT")
    public void sendDailyIncomeExpenseReminder() {
        log.info("Sending Daily Income Expense Reminder");
        List<Profile> profileLis = profileRepository.findAll();

        for (Profile profile : profileLis) {
            String body = "Hi " + profile.getFullName() + ", <br><br>"
                    + "This is a friendly reminder to add your income and expense for today in Money Manager.<br><br>"
                    + "<a href='" + frontendUrl + "' style='display:inline-block; padding:10px 20px; background-color:#4CAF50; color:#fff; text-decoration:none; border-radius:5px; font-weight:bold;'>"
                    + "Go to Money Manager</a>"
                    + "<br><br>Best Regards,<br>Money Manager Team";
            emailService.sendMail(profile.getEmail(), "Daily remainder: Add your income and expenses", body);
        }
        log.info("Finished Sending Daily Income Expense Reminder");
    }

    @Scheduled(cron = "0 0 23 * * *", zone = "GMT")// runs daily at 11 PM GMT
    public void sendDailyExpenseSummary() {
        log.info("Sending Daily Expense Summary...");

        List<Profile> profiles = profileRepository.findAll();

        for (Profile profile : profiles) {
            List<ExpenseDto> todayExpense = expenseService.getExpensesForUserOnDate(profile.getId(), LocalDate.now());

            if (!todayExpense.isEmpty()) { // ✅ only send if expenses exist
                StringBuilder html = new StringBuilder();

                html.append("<html><body style='font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px;'>");
                html.append("<div style='max-width: 600px; margin: auto; background-color: #fff; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); padding: 20px;'>");

                html.append("<h2 style='color: #333;'>Hi ").append(profile.getFullName()).append(",</h2>");
                html.append("<p>Here’s your <b>Daily Expense Summary</b> for ").append(LocalDate.now()).append(":</p>");

                // ======= Table =======
                html.append("<table style='border-collapse: collapse; width: 100%; margin-top: 10px;'>");
                html.append("<thead style='background-color: #4CAF50; color: white;'>");
                html.append("<tr>")
                        .append("<th style='padding: 10px; border: 1px solid #ddd;'>#</th>")
                        .append("<th style='padding: 10px; border: 1px solid #ddd;'>Expense Name</th>")
                        .append("<th style='padding: 10px; border: 1px solid #ddd;'>Amount</th>")
                        .append("<th style='padding: 10px; border: 1px solid #ddd;'>Category</th>")
                        .append("</tr>")
                        .append("</thead><tbody>");

                int i = 1;
                BigDecimal total = BigDecimal.ZERO;

                for (ExpenseDto expense : todayExpense) {
                    html.append("<tr>")
                            .append("<td style='padding: 10px; border: 1px solid #ddd; text-align: center;'>").append(i++).append("</td>")
                            .append("<td style='padding: 10px; border: 1px solid #ddd;'>").append(expense.getName()).append("</td>")
                            .append("<td style='padding: 10px; border: 1px solid #ddd;'>").append(expense.getAmount()).append("</td>")
                            .append("<td style='padding: 10px; border: 1px solid #ddd;'>")
                            .append(expense.getCategoryName() != null ? expense.getCategoryName() : "N/A")
                            .append("</td>")
                            .append("</tr>");

                    // ✅ Use BigDecimal addition
                    total = total.add(expense.getAmount());
                }

                // ======= Total Row =======
                html.append("<tr style='font-weight:bold; background-color:#f2f2f2;'>")
                        .append("<td colspan='2' style='padding:10px; border:1px solid #ddd; text-align:right;'>Total:</td>")
                        .append("<td colspan='2' style='padding:10px; border:1px solid #ddd;'>").append(total).append("</td>")
                        .append("</tr>");

                html.append("</tbody></table>");
                html.append("<br><p style='font-size: 14px;'>Keep tracking your spending to stay on top of your finances!</p>");

                html.append("<a href='").append(frontendUrl)
                        .append("' style='display:inline-block; padding:10px 20px; background-color:#4CAF50; color:white; text-decoration:none; border-radius:5px; font-weight:bold;'>")
                        .append("Go to Money Manager</a>");

                html.append("<br><br><p>Best Regards,<br><b>Money Manager Team</b></p>");
                html.append("</div></body></html>");

                // ✅ Send email
                emailService.sendMail(
                        profile.getEmail(),
                        "Daily Expense Summary - " + LocalDate.now(),
                        html.toString()
                );

                log.info("✅ Sent expense summary email to {}", profile.getEmail());
            } else {
                log.info("No expenses found for {}", profile.getFullName());
            }
        }
    }

}
