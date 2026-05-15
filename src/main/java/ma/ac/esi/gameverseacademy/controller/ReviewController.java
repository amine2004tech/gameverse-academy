package ma.ac.esi.gameverseacademy.controller;

import ma.ac.esi.gameverseacademy.model.Review;
import ma.ac.esi.gameverseacademy.model.User;
import ma.ac.esi.gameverseacademy.service.ReviewService;

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

        int modId = Integer.parseInt(request.getParameter("modId"));
        int rating = Integer.parseInt(request.getParameter("rating"));
        String comment = request.getParameter("comment");

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