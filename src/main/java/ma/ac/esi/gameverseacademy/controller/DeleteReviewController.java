package ma.ac.esi.gameverseacademy.controller;

import ma.ac.esi.gameverseacademy.model.User;
import ma.ac.esi.gameverseacademy.service.ReviewService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/DeleteReviewController")
public class DeleteReviewController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {

            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");

        int reviewId = Integer.parseInt(request.getParameter("reviewId"));
        int modId = Integer.parseInt(request.getParameter("modId"));

        ReviewService reviewService = new ReviewService();

        reviewService.deleteReview(reviewId, user.getId());

        response.sendRedirect(
                request.getContextPath() +
                "/ModDetailsController?id=" + modId
        );
    }
}