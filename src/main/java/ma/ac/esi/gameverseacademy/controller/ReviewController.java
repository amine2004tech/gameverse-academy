package ma.ac.esi.gameverseacademy.controller;

import ma.ac.esi.gameverseacademy.model.Review;
import ma.ac.esi.gameverseacademy.model.User;
import ma.ac.esi.gameverseacademy.service.ReviewService;
import ma.ac.esi.gameverseacademy.security.InputSanitizer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/ReviewController")
public class ReviewController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // user must be logged in
        if (session == null || session.getAttribute("user") == null) {

            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");

        // SEC-FIX: Safe parsing of user-supplied numeric params
        int modId;
        int rating;
        try {
            modId = Integer.parseInt(request.getParameter("modId"));
            rating = Integer.parseInt(request.getParameter("rating"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/ModController");
            return;
        }
        // SEC-FIX: Validate rating range server-side
        if (rating < 1 || rating > 5) {
            response.sendRedirect(request.getContextPath() + "/ModDetailsController?id=" + modId);
            return;
        }
        String comment = InputSanitizer.sanitize(request.getParameter("comment"));

        Review review = new Review();

        review.setModId(modId);


        review.setUserId(user.getId());


        review.setUsername(user.getUsername());

        review.setRating(rating);
        review.setComment(comment);

        ReviewService reviewService = new ReviewService();

        reviewService.addReview(review);

        response.sendRedirect(
                request.getContextPath() +
                "/ModDetailsController?id=" + modId
        );
    }
}