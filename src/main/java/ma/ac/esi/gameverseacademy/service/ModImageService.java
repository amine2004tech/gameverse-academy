package ma.ac.esi.gameverseacademy.service;

import ma.ac.esi.gameverseacademy.model.ModImage;
import ma.ac.esi.gameverseacademy.repository.ModImageRepository;

import java.util.List;

public class ModImageService {

    private ModImageRepository modImageRepository;

    public ModImageService() {
        this.modImageRepository = new ModImageRepository();
    }

    public List<ModImage> getImagesByModId(int modId) {

        return modImageRepository.getImagesByModId(modId);
    }

    public ModImage getThumbnailByModId(int modId) {

        return modImageRepository.getThumbnailByModId(modId);
    }

    public boolean addImage(ModImage image) {

        if (image == null)
            return false;

        if (image.getModId() <= 0)
            return false;

        if (image.getImageName() == null ||
            image.getImageName().trim().isEmpty())
            return false;

        if (image.getPosition() < 0)
            return false;

        return modImageRepository.addImage(image);
    }
}