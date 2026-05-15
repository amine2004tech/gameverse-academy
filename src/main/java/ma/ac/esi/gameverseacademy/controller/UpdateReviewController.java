package ma.ac.esi.gameverseacademy.controller;

import ma.ac.esi.gameverseacademy.model.Review;
import ma.ac.esi.gameverseacademy.model.User;
import ma.ac.esi.gameverseacademy.service.ReviewService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/UpdateReviewController")
public class UpdateReviewController extends HttpServlet {

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
        int rating = Integer.parseInt(request.getParameter("rating"));
        String comment = request.getParameter("comment");

        Review review = new Review();

        review.setId(reviewId);
        review.setModId(modId);

        // ownership security
        review.setUserId(user.getId());

        review.setRating(rating);
        review.setComment(comment);

        ReviewService reviewService = new ReviewService();

        reviewService.updateReview(review);

        response.sendRedirect(
                request.getContextPath() +
                "/ModDetailsController?id=" + modId
        );
    }
}