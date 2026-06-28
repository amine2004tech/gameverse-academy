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

    // =========================
    // PROCESS MOD SUBMISSION (VALIDATION + PERSISTENCE)
    // =========================
    public List<String> processModSubmission(ma.ac.esi.gameverseacademy.model.User currentUser, String title, String gameIdParam, String modIdParam, String description, String youtubeUrl, String selectedTagsStr, String imageOrderStr, List<FileUploadEntry> imageParts, FileUploadEntry zipPart, List<String> uploadErrors, String baseUploadPath) {
        List<String> errors = new ArrayList<>(uploadErrors);
        
        // Validate title
        if (title == null || title.trim().isEmpty()) {
            errors.add("Title is required.");
        } else if (title.trim().length() > 100) {
            errors.add("Title must be 100 characters or less.");
        }

        // Validate game selection
        int gameId = 0;
        try { gameId = Integer.parseInt(gameIdParam); } catch (Exception e) {}
        if (gameId <= 0) {
            errors.add("Game selection is required.");
        }

        int modId = 0;
        if (modIdParam != null && !modIdParam.isEmpty()) {
            try { modId = Integer.parseInt(modIdParam); } catch (Exception e) {}
        }

        if (modId == 0) {
            List<Mod> userMods = modRepository.getModsByUserId(currentUser.getId());
            int pendingCount = 0;
            if (userMods != null) {
                for (Mod m : userMods) {
                    if ("PENDING".equalsIgnoreCase(m.getStatus())) {
                        pendingCount++;
                    }
                }
            }
            if (pendingCount >= 2) {
                errors.add("You cannot have more than 2 pending mod submissions at a time.");
                return errors;
            }

            if (zipPart == null) {
                boolean hasModFileError = false;
                for (String err : errors) {
                    if (err.contains("mod package") || err.contains("archive") || err.contains("ZIP")) {
                        hasModFileError = true;
                        break;
                    }
                }
                if (!hasModFileError) {
                    errors.add("Mod package (ZIP) is required for new submissions.");
                }
            }
            if (imageParts.isEmpty()) {
                boolean hasImageError = false;
                for (String err : errors) {
                    if (err.contains("Image file")) {
                        hasImageError = true;
                        break;
                    }
                }
                if (!hasImageError) {
                    errors.add("At least one valid image is required for the mod gallery.");
                }
            }
        }

        if (!errors.isEmpty()) {
            return errors; 
        }

        // Prepare Mod Object
        Mod mod = new Mod();
        mod.setTitle(title.trim());
        mod.setGameId(gameId);
        mod.setUserId(currentUser.getId());
        mod.setDescription(description);
        mod.setYoutubeVideoId(youtubeUrl);

        List<Integer> tagIds = parseTagIds(selectedTagsStr);

        boolean success;
        if (modId > 0) {
            mod.setId(modId);
            Mod existing = getModById(modId);
            if (existing != null && existing.getUserId() == currentUser.getId()) {
                success = updateMod(mod, tagIds, imageParts, zipPart, baseUploadPath, imageOrderStr);
            } else {
                errors.add("You are not authorized to update this mod.");
                return errors;
            }
        } else {
            success = submitMod(mod, tagIds, imageParts, zipPart, baseUploadPath, imageOrderStr) > 0;
        }

        if (!success) {
            errors.add("Submission failed. Please check your inputs and try again.");
        }

        return errors;
    }

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

            // Enrich with Thumbnail & All Images
            java.util.List<ma.ac.esi.gameverseacademy.model.ModImage> images = modImageService.getImagesByModId(mod.getId());
            mod.setImages(images);
            if (images != null && !images.isEmpty()) {
                mod.setThumbnail(images.get(0).getImageName());
            }

            // Enrich with Tags
            mod.setTags(tagService.getTagsByModId(mod.getId()));
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
            
            // Enrich with Thumbnail & All Images
            java.util.List<ma.ac.esi.gameverseacademy.model.ModImage> images = modImageService.getImagesByModId(mod.getId());
            mod.setImages(images);
            if (images != null && !images.isEmpty()) {
                mod.setThumbnail(images.get(0).getImageName());
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
    public int submitMod(Mod mod, List<Integer> tagIds, List<FileUploadEntry> imageParts, FileUploadEntry zipPart, String baseUploadPath, String imageOrderStr) {
        if (!isValidMod(mod)) {
            return -1;
        }

        mod.setStatus("PENDING");
        mod.setYoutubeVideoId(extractYoutubeVideoId(mod.getYoutubeVideoId()));

        int targetModId = modRepository.insertMod(mod);
        if (targetModId <= 0) return -1;

        // Enrich mod object with generated ID
        mod.setId(targetModId);

        List<File> savedFiles = new ArrayList<>();
        boolean hasError = false;

        try {
            // Handle Tags
            if (tagIds != null && !tagIds.isEmpty()) {
                tagService.clearAndAddTags(targetModId, tagIds);
            }

            // Handle Images
            if (imageParts != null && !imageParts.isEmpty()) {
                List<Integer> order = new ArrayList<>();
                if (imageOrderStr != null && !imageOrderStr.isEmpty()) {
                    for (String s : imageOrderStr.split(",")) {
                        String token = s.trim();
                        if (token.startsWith("new:")) {
                            token = token.substring(4);
                        } else if (token.startsWith("db:")) {
                            continue; // new mods shouldn't have db tokens, but just in case
                        }
                        try { order.add(Integer.parseInt(token)); } catch (Exception e) {}
                    }
                }
                // Fallback to sequential if order is broken
                if (order.size() != imageParts.size()) {
                    order.clear();
                    for (int i = 0; i < imageParts.size(); i++) order.add(i);
                }

                for (int pos = 0; pos < order.size(); pos++) {
                    int partIndex = order.get(pos);
                    if (partIndex >= 0 && partIndex < imageParts.size()) {
                        FileUploadEntry entry = imageParts.get(partIndex);
                        
                        String ext = "";
                        String original = entry.getOriginalFileName();
                        if (original != null && original.contains(".")) {
                            ext = original.substring(original.lastIndexOf('.'));
                        }
                        
                        String fileName = targetModId + "_" + pos + "_" + System.currentTimeMillis() + ext;
                        String fullPath = baseUploadPath + File.separator + "images" + File.separator + "mods";
                        
                        if (saveFile(entry.getInputStream(), fullPath, fileName)) {
                            savedFiles.add(new File(fullPath, fileName));
                            ModImage mi = new ModImage();
                            mi.setModId(targetModId);
                            mi.setImageName(fileName);
                            mi.setPosition(pos); // Set the explicit sort_order
                            modImageService.addImage(mi);
                        } else {
                            hasError = true;
                            break;
                        }
                    }
                }
            }

            // Handle Zip File - Keep original name (prefixed with ID to prevent collisions)
            if (!hasError && zipPart != null) {
                String originalName = zipPart.getOriginalFileName();
                String physicalName = targetModId + "_" + originalName;
                String fullPath = baseUploadPath + File.separator + "mods";
                if (saveFile(zipPart.getInputStream(), fullPath, physicalName)) {
                    savedFiles.add(new File(fullPath, physicalName));
                    mod.setFileName(physicalName);
                    modRepository.updateMod(mod);
                } else {
                    hasError = true;
                }
            }

            if (hasError) {
                throw new Exception("File persistence failed during mod submission.");
            }

            return targetModId;

        } catch (Exception e) {
            e.printStackTrace();
            // Transaction Rollback
            for (File f : savedFiles) {
                if (f.exists()) f.delete();
            }
            modRepository.deleteMod(targetModId);
            return -1;
        }
    }

    public boolean updateMod(Mod mod, List<Integer> tagIds, List<FileUploadEntry> imageParts, FileUploadEntry zipPart, String baseUploadPath, String imageOrderStr) {
        if (mod == null || mod.getId() <= 0 || !isValidMod(mod)) {
            return false;
        }

        mod.setYoutubeVideoId(extractYoutubeVideoId(mod.getYoutubeVideoId()));

        // If a new ZIP is uploaded, update filename and set status back to PENDING
        if (zipPart != null) {
            String originalName = zipPart.getOriginalFileName();
            mod.setFileName(mod.getId() + "_" + originalName);
            mod.setStatus("PENDING");
            modRepository.updateModStatus(mod.getId(), "PENDING");
        } else {
            // Keep the existing file name if no new file is uploaded
            Mod existing = getModById(mod.getId());
            if (existing != null) {
                mod.setFileName(existing.getFileName());
            }
        }

        boolean updated = modRepository.updateMod(mod);
        if (!updated) return false;

        // Handle Tags
        if (tagIds != null) {
            tagService.clearAndAddTags(mod.getId(), tagIds);
        }

        // Handle Images (Unified Ordering)
        if (imageOrderStr != null && !imageOrderStr.isEmpty()) {
            String[] tokens = imageOrderStr.split(",");
            modImageService.deleteImagesByModId(mod.getId());
            
            int actualPos = 0;
            for (int i = 0; i < tokens.length; i++) {
                String token = tokens[i].trim();
                if (token.startsWith("db:")) {
                    String imgName = token.substring(3);
                    ModImage mi = new ModImage();
                    mi.setModId(mod.getId());
                    mi.setImageName(imgName);
                    mi.setPosition(actualPos++);
                    modImageService.addImage(mi);
                } else if (token.startsWith("new:")) {
                    int partIndex = -1;
                    try { partIndex = Integer.parseInt(token.substring(4)); } catch (Exception e) {}
                    
                    if (imageParts != null && partIndex >= 0 && partIndex < imageParts.size()) {
                        FileUploadEntry entry = imageParts.get(partIndex);
                        
                        String ext = "";
                        String original = entry.getOriginalFileName();
                        if (original != null && original.contains(".")) {
                            ext = original.substring(original.lastIndexOf('.'));
                        }
                        
                        String fileName = mod.getId() + "_" + actualPos + "_" + System.currentTimeMillis() + ext;
                        String fullPath = baseUploadPath + File.separator + "images" + File.separator + "mods";
                        
                        if (saveFile(entry.getInputStream(), fullPath, fileName)) {
                            ModImage mi = new ModImage();
                            mi.setModId(mod.getId());
                            mi.setImageName(fileName);
                            mi.setPosition(actualPos++);
                            modImageService.addImage(mi);
                        }
                    }
                }
            }
        } else if (imageOrderStr != null && imageOrderStr.trim().isEmpty()) {
            // User explicitly removed all images
            modImageService.deleteImagesByModId(mod.getId());
        }

        // Handle Zip File (Overwrite)
        if (zipPart != null) {
            String originalName = zipPart.getOriginalFileName();
            String physicalName = mod.getId() + "_" + originalName;
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

        // Removed: We no longer rename files on approval, we keep the original names!
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
        Mod mod = getModById(modId);
        if (mod != null && mod.getFileName() != null && !mod.getFileName().trim().isEmpty()) {
            File dbNamedFile = new File(baseUploadPath + File.separator + "mods", mod.getFileName());
            if (dbNamedFile.exists()) {
                return dbNamedFile;
            }
        }
        // Fallback to id.zip
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