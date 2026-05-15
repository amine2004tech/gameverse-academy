package ma.ac.esi.gameverseacademy.service;

import ma.ac.esi.gameverseacademy.model.Tag;
import ma.ac.esi.gameverseacademy.repository.TagRepository;

import java.util.List;

public class TagService {

    private TagRepository tagRepository;

    public TagService() {
        this.tagRepository = new TagRepository();
    }

    public List<Tag> getTagsByModId(int modId) {
        if (modId <= 0) {
            return java.util.Collections.emptyList();
        }
        return tagRepository.getTagsByModId(modId);
    }
    public List<Tag> getAllTags() {
        return tagRepository.getAllTags();
    }

    public void addTagsToMod(int modId, List<Integer> tagIds) {
        if (modId > 0 && tagIds != null && !tagIds.isEmpty()) {
            tagRepository.addTagsToMod(modId, tagIds);
        }
    }

    public void clearAndAddTags(int modId, List<Integer> tagIds) {
        if (modId > 0) {
            tagRepository.deleteTagsByModId(modId);
            if (tagIds != null && !tagIds.isEmpty()) {
                tagRepository.addTagsToMod(modId, tagIds);
            }
        }
    }
}
