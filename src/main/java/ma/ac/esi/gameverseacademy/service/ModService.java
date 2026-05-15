package ma.ac.esi.gameverseacademy.service;

import ma.ac.esi.gameverseacademy.model.Mod;
import ma.ac.esi.gameverseacademy.model.ModImage;
import ma.ac.esi.gameverseacademy.model.Tag;
import ma.ac.esi.gameverseacademy.repository.ModRepository;
import ma.ac.esi.gameverseacademy.repository.ReviewRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModService {

    private ModRepository modRepository = new ModRepository();
    private ModImageService modImageService = new ModImageService();
    private ReviewRepository reviewRepository = new ReviewRepository();
    private ReviewService reviewService = new ReviewService();
    private GameService gameService = new GameService();
    private TagService tagService = new TagService();

    // =========================
    // GET ALL
    // =========================
    public List<Mod> getAllMods() {
        return modRepository.getAllMods();
    }

    // =========================
    // GET BY ID
    // =========================
    public Mod getModById(int id) {

        if (id <= 0)
            return null;

        return modRepository.getModById(id);
    }

    // =========================
    // GET BY USER (PROFILE NEED)
    // =========================
    public List<Mod> getModsByUserId(int userId) {

        if (userId <= 0) {
            return new ArrayList<>();
        }

        List<Mod> mods = modRepository.getModsByUserId(userId);

        for (Mod mod : mods) {
            // Enrich with Average Rating
            double avg = reviewRepository.getAverageRating(mod.getId());
            mod.setAverageRating(avg);

            // Enrich with Thumbnail
            ModImage thumb = modImageService.getThumbnailByModId(mod.getId());
            if (thumb != null) {
                mod.setThumbnail(thumb.getImageName());
            }
        }

        return mods;
    }

    // =========================
    // FILTER BY GAME
    // =========================
    public List<Mod> getModsByGameId(int gameId) {

        List<Mod> all = modRepository.getApprovedMods();
        List<Mod> filtered = new ArrayList<>();

        if (gameId <= 0)
            return filtered;

        for (Mod mod : all) {
            if (mod.getGameId() == gameId) {
                filtered.add(mod);
            }
        }

        return filtered;
    }

    // =========================
    // FILTERED & ENRICHED MODS
    // =========================
    public List<Mod> getFilteredMods(Integer gameId, List<Integer> tagIds) {
        List<Mod> allApproved = getApprovedMods();
        List<Mod> filtered = new ArrayList<>();

        for (Mod mod : allApproved) {
            boolean gameMatch = (gameId == null || mod.getGameId() == gameId);
            boolean allTagsMatch = true;

            if (tagIds != null && !tagIds.isEmpty()) {
                List<Tag> modTags = mod.getTags();
                if (modTags == null) {
                    allTagsMatch = false;
                } else {
                    for (Integer requiredTagId : tagIds) {
                        boolean found = false;
                        for (Tag t : modTags) {
                            if (t.getId() == requiredTagId) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            allTagsMatch = false;
                            break;
                        }
                    }
                }
            }

            if (gameMatch && allTagsMatch) {
                filtered.add(mod);
            }
        }
        return filtered;
    }

    private void enrichModList(List<Mod> mods) {
        for (Mod mod : mods) {
            // Enrich with Average Rating
            mod.setAverageRating(reviewService.getAverageRating(mod.getId()));
            
            // Enrich with Thumbnail
            ModImage thumb = modImageService.getThumbnailByModId(mod.getId());
            if (thumb != null) {
                mod.setThumbnail(thumb.getImageName());
            }

            // Enrich with Tags
            mod.setTags(tagService.getTagsByModId(mod.getId()));
        }
    }

    // =========================
    // APPROVED MODS
    // =========================
    public List<Mod> getApprovedMods() {
        List<Mod> mods = modRepository.getApprovedMods();
        enrichModList(mods);
        return mods;
    }

    // =========================
    // PENDING MODS
    // =========================
    public List<Mod> getPendingMods() {
        List<Mod> mods = modRepository.getModsByStatus("PENDING");
        enrichModList(mods);
        return mods;
    }

    public List<Mod> getFilteredPendingMods(Integer gameId, List<Integer> tagIds) {
        List<Mod> allPending = getPendingMods();
        List<Mod> filtered = new ArrayList<>();

        for (Mod mod : allPending) {
            boolean gameMatch = (gameId == null || mod.getGameId() == gameId);
            boolean allTagsMatch = true;

            if (tagIds != null && !tagIds.isEmpty()) {
                List<Tag> modTags = mod.getTags();
                if (modTags == null) {
                    allTagsMatch = false;
                } else {
                    for (Integer requiredTagId : tagIds) {
                        boolean found = false;
                        for (Tag t : modTags) {
                            if (t.getId() == requiredTagId) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            allTagsMatch = false;
                            break;
                        }
                    }
                }
            }

            if (gameMatch && allTagsMatch) {
                filtered.add(mod);
            }
        }
        return filtered;
    }

    // =========================
    // SUBMIT MOD
    // =========================
    public int submitMod(Mod mod, List<Integer> tagIds, List<FileUploadEntry> imageParts, FileUploadEntry zipPart, String baseUploadPath) {
        if (!isValidMod(mod)) {
            return -1;
        }

        mod.setStatus("PENDING");
        mod.setYoutubeVideoId(extractYoutubeVideoId(mod.getYoutubeVideoId()));

        int targetModId = modRepository.insertMod(mod);
        if (targetModId <= 0) return -1;

        // Handle Tags
        if (tagIds != null && !tagIds.isEmpty()) {
            tagService.clearAndAddTags(targetModId, tagIds);
        }

        // Handle Images
        if (imageParts != null) {
            int index = 0;
            for (FileUploadEntry entry : imageParts) {
                String fileName = targetModId + "_" + index + ".jpg";
                String fullPath = baseUploadPath + File.separator + "images" + File.separator + "mods";
                if (saveFile(entry.getInputStream(), fullPath, fileName)) {
                    ModImage mi = new ModImage();
                    mi.setModId(targetModId);
                    mi.setImageName(fileName);
                    mi.setPosition(index);
                    modImageService.addImage(mi);
                    index++;
                }
            }
        }

        // Handle Zip File
        if (zipPart != null) {
            String physicalName = targetModId + ".zip";
            String fullPath = baseUploadPath + File.separator + "mods";
            saveFile(zipPart.getInputStream(), fullPath, physicalName);
        }

        return targetModId;
    }

    public boolean updateMod(Mod mod, List<Integer> tagIds, List<FileUploadEntry> imageParts, FileUploadEntry zipPart, String baseUploadPath) {
        if (mod == null || mod.getId() <= 0 || !isValidMod(mod)) {
            return false;
        }

        mod.setYoutubeVideoId(extractYoutubeVideoId(mod.getYoutubeVideoId()));
        boolean updated = modRepository.updateMod(mod);
        if (!updated) return false;

        // Handle Tags
        if (tagIds != null) {
            tagService.clearAndAddTags(mod.getId(), tagIds);
        }

        // Handle Images (Append new ones)
        if (imageParts != null) {
            int nextIndex = modImageService.getImagesByModId(mod.getId()).size();
            for (FileUploadEntry entry : imageParts) {
                String fileName = mod.getId() + "_" + nextIndex + ".jpg";
                String fullPath = baseUploadPath + File.separator + "images" + File.separator + "mods";
                if (saveFile(entry.getInputStream(), fullPath, fileName)) {
                    ModImage mi = new ModImage();
                    mi.setModId(mod.getId());
                    mi.setImageName(fileName);
                    mi.setPosition(nextIndex);
                    modImageService.addImage(mi);
                    nextIndex++;
                }
            }
        }

        // Handle Zip File (Overwrite)
        if (zipPart != null) {
            String physicalName = mod.getId() + ".zip";
            String fullPath = baseUploadPath + File.separator + "mods";
            saveFile(zipPart.getInputStream(), fullPath, physicalName);
        }

        return true;
    }

    private boolean saveFile(InputStream input, String path, String fileName) {
        try {
            File dir = new File(path);
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, fileName);
            Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static class FileUploadEntry {
        private InputStream inputStream;
        private String originalFileName;
        public FileUploadEntry(InputStream is, String name) { this.inputStream = is; this.originalFileName = name; }
        public InputStream getInputStream() { return inputStream; }
        public String getOriginalFileName() { return originalFileName; }
    }

    // =========================
    // APPROVE
    // =========================
    public boolean approveMod(int modId, String baseUploadPath) {
        if (modId <= 0) return false;

        Mod mod = getModById(modId);
        if (mod == null) return false;

        boolean success = modRepository.updateModStatus(modId, "APPROVED");
        if (!success) return false;

        // Handle File Renaming Business Rule
        String oldFileName = mod.getFileName();
        if (oldFileName != null && oldFileName.startsWith("pending_")) {
            String safeTitle = mod.getTitle().trim().toLowerCase().replaceAll("[^a-z0-9]", "_");
            String newFileName = safeTitle + ".zip";
            
            mod.setFileName(newFileName);
            modRepository.updateMod(mod);

            try {
                File modDir = new File(baseUploadPath + File.separator + "mods");
                File oldFile = new File(modDir, oldFileName);
                if (oldFile.exists()) {
                    File newFile = new File(modDir, newFileName);
                    oldFile.renameTo(newFile);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    // =========================
    // REJECT
    // =========================
    public boolean rejectMod(int modId) {

        if (modId <= 0)
            return false;

        return modRepository.updateModStatus(modId, "REJECTED");
    }

    // =========================
    // UPDATE
    // =========================
    public boolean updateMod(Mod mod) {

        if (mod == null || mod.getId() <= 0)
            return false;

        if (!isValidMod(mod))
            return false;

        if (!isValidYoutubeId(mod.getYoutubeVideoId())) {
            mod.setYoutubeVideoId(null);
        }

        return modRepository.updateMod(mod);
    }

    // =========================
    // DELETE
    // =========================
    public boolean deleteMod(int modId, ma.ac.esi.gameverseacademy.model.User user) {
        if (modId <= 0 || user == null) return false;
        
        Mod mod = getModById(modId);
        if (mod != null && (mod.getUserId() == user.getId() || "ADMIN".equalsIgnoreCase(user.getRole()))) {
            return modRepository.deleteMod(modId);
        }
        return false;
    }

    // =========================
    // DOWNLOAD
    // =========================
    public void registerDownload(int modId) {

        if (modId > 0) {
            modRepository.increaseDownload(modId);
        }
    }

    // =========================
    // VALIDATION
    // =========================
    private boolean isValidMod(Mod mod) {

        if (mod == null)
            return false;

        if (mod.getGameId() <= 0)
            return false;

        if (mod.getUserId() <= 0)
            return false;

        if (mod.getTitle() == null ||
                mod.getTitle().trim().isEmpty())
            return false;

        if (mod.getTitle().length() > 100)
            return false;

        if (mod.getDescription() != null &&
                mod.getDescription().length() > 5000)
            return false;

        return true;
    }

    public String extractYoutubeVideoId(String url) {
        if (url == null || url.trim().isEmpty()) {
            return null;
        }
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);
        if (matcher.find()) {
            return matcher.group();
        }
        // If it's already an 11-char ID, return it
        if (url.matches("^[a-zA-Z0-9_-]{11}$")) {
            return url;
        }
        return null;
    }

    private boolean isValidYoutubeId(String youtubeId) {
        if (youtubeId == null || youtubeId.trim().isEmpty()) {
            return true;
        }
        return youtubeId.matches("^[a-zA-Z0-9_-]{11}$");
    }

    public Mod getModDetails(int modId) {

        Mod mod = modRepository.getModById(modId);

        if (mod == null) {
            return null;
        }

        // enrichment
        mod.setAverageRating(reviewService.getAverageRating(modId));
        mod.setReviews(reviewService.getReviewsByModId(modId));
        mod.setRatingDistribution(reviewService.getRatingDistribution(modId));

        mod.setImages(modImageService.getImagesByModId(modId));
        mod.setTags(tagService.getTagsByModId(modId));
        mod.setGame(gameService.getGameById(mod.getGameId()));

        return mod;
    }

    public boolean canUserAccessMod(Mod mod, ma.ac.esi.gameverseacademy.model.User user) {
        if (mod == null) return false;
        if (!"PENDING".equalsIgnoreCase(mod.getStatus()))
            return true;

        if (user == null)
            return false;
        return "ADMIN".equalsIgnoreCase(user.getRole()) || user.getId() == mod.getUserId();
    }

    public File getPhysicalModFile(int modId, String baseUploadPath) {
        return new File(baseUploadPath + File.separator + "mods", modId + ".zip");
    }

    public static List<Integer> parseTagIds(String tagsParam) {
        List<Integer> selectedTags = new ArrayList<>();
        if (tagsParam != null && !tagsParam.isEmpty()) {
            for (String t : tagsParam.split(",")) {
                try {
                    selectedTags.add(Integer.parseInt(t.trim()));
                } catch (NumberFormatException e) {
                }
            }
        }
        return selectedTags;
    }
}