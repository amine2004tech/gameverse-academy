package ma.ac.esi.gameverseacademy.service;

import ma.ac.esi.gameverseacademy.model.Review;
import ma.ac.esi.gameverseacademy.repository.ReviewRepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReviewService {

    private ReviewRepository reviewRepository;

    public ReviewService() {
        this.reviewRepository = new ReviewRepository();
    }

    // =========================
    // CREATE
    // =========================
    public boolean addReview(Review review) {

        if (!isValidReview(review)) {
            return false;
        }

        // one user = one review per mod
        if (reviewRepository.hasUserReviewed(
                review.getUserId(),
                review.getModId())) {

            return false;
        }

        return reviewRepository.addReview(review);
    }

    // =========================
    // GET REVIEWS
    // =========================
    public List<Review> getReviewsByModId(int modId) {

        return reviewRepository.getReviewsByModId(modId);
    }

    // =========================
    // AVERAGE RATING
    // =========================
    public double getAverageRating(int modId) {

        return reviewRepository.getAverageRating(modId);
    }

    // =========================
    // RATING DISTRIBUTION
    // =========================
    public Map<Integer, Integer> getRatingDistribution(int modId) {

        List<Review> reviews =
                reviewRepository.getReviewsByModId(modId);

        Map<Integer, Integer> distribution =
                new LinkedHashMap<>();

        // 5 -> 1 stars
        for (int i = 5; i >= 1; i--) {
            distribution.put(i, 0);
        }

        for (Review review : reviews) {

            int rating = review.getRating();

            if (rating >= 1 && rating <= 5) {

                distribution.put(
                        rating,
                        distribution.get(rating) + 1
                );
            }
        }

        return distribution;
    }

    // =========================
    // GET USER REVIEW
    // =========================
    public Review getUserReview(int modId, int userId) {

        List<Review> reviews =
                reviewRepository.getReviewsByModId(modId);

        for (Review review : reviews) {

            if (review.getUserId() == userId) {
                return review;
            }
        }

        return null;
    }

    // =========================
    // UPDATE
    // =========================
    public boolean updateReview(Review review) {

        if (review.getId() <= 0 ||
            !isValidReview(review)) {

            return false;
        }

        return reviewRepository.updateReview(review);
    }

    // =========================
    // DELETE
    // =========================
    public boolean deleteReview(int reviewId, int userId) {

        if (reviewId <= 0 || userId <= 0) {
            return false;
        }

        return reviewRepository.deleteReview(
                reviewId,
                userId
        );
    }

    // =========================
    // HAS USER REVIEWED
    // =========================
    public boolean hasUserReviewed(int userId,
                                   int modId) {

        return reviewRepository.hasUserReviewed(
                userId,
                modId
        );
    }

    // =========================
    // VALIDATION
    // =========================
    private boolean isValidReview(Review review) {

        if (review == null)
            return false;

        if (review.getModId() <= 0)
            return false;

        // secure identity check
        if (review.getUserId() <= 0)
            return false;

        if (review.getRating() < 1 ||
            review.getRating() > 5)
            return false;

        if (review.getComment() == null)
            return false;

        return review.getComment()
                     .trim()
                     .length() <= 500;
    }
}