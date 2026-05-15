/* Submit Page JavaScript */

document.addEventListener('DOMContentLoaded', function() {
    // Make sure we have the correct files hint when files are selected
    const imageInput = document.getElementById('modImages');
    if (imageInput) {
        imageInput.addEventListener('change', function(e) {
            const hint = this.nextElementSibling;
            if (this.files && this.files.length > 0) {
                hint.textContent = this.files.length + " IMAGES SELECTED";
                hint.style.color = 'var(--accent-primary)';
            } else {
                hint.textContent = "SELECT IMAGES";
                hint.style.color = '';
            }
        });
    }

    const zipInput = document.getElementById('modFile');
    if (zipInput) {
        zipInput.addEventListener('change', function(e) {
            const hint = this.nextElementSibling;
            if (this.files && this.files.length > 0) {
                hint.textContent = this.files[0].name;
                hint.style.color = 'var(--accent-primary)';
            } else {
                hint.textContent = "SELECT ZIP";
                hint.style.color = '';
            }
        });
    }

    // Form submit listener to gather tags
    const form = document.getElementById('submitModForm');
    if (form) {
        form.addEventListener('submit', function(e) {
            const selectedTagEls = document.querySelectorAll('.tag-selector-item.active');
            const tagsInput = document.getElementById('selectedTags');
            
            const tagIds = Array.from(selectedTagEls).map(el => el.getAttribute('data-tag-id'));
            tagsInput.value = tagIds.join(',');
        });
    }
});

function openSubmitModal() {
    const modal = document.getElementById('submitModModal');
    if (modal) {
        // Reset form for new submission
        document.getElementById('submitModForm').reset();
        document.getElementById('modId').value = "";
        document.getElementById('modalTitle').textContent = "ARCHIVE NEW ENTRY";
        
        // Reset tags
        document.querySelectorAll('.tag-selector-item').forEach(el => el.classList.remove('active'));
        
        // Reset file hints
        document.querySelectorAll('.file-upload-hint').forEach(el => {
            el.textContent = el.parentElement.querySelector('input').accept.includes('.zip') ? "SELECT ZIP" : "SELECT IMAGES";
            el.style.color = '';
        });

        modal.classList.add('active');
        document.body.style.overflow = 'hidden';
    }
}

function closeSubmitModal() {
    const modal = document.getElementById('submitModModal');
    if (modal) {
        modal.classList.remove('active');
        document.body.style.overflow = '';
    }
}

function openEditModal(id, title, description, gameId, youtubeUrl) {
    const modal = document.getElementById('submitModModal');
    if (modal) {
        document.getElementById('modalTitle').textContent = "EDIT ARCHIVE ENTRY";
        document.getElementById('modId').value = id;
        document.getElementById('title').value = title;
        document.getElementById('description').value = description;
        document.getElementById('gameId').value = gameId;
        
        if (youtubeUrl && youtubeUrl !== "null") {
            // Just populate the ID for now, user can replace it with a new URL if needed
            document.getElementById('youtubeUrl').value = youtubeUrl;
        } else {
            document.getElementById('youtubeUrl').value = "";
        }
        
        // For editing, tags and files handling would need backend support to prefill
        // Currently resetting them in frontend view
        document.querySelectorAll('.tag-selector-item').forEach(el => el.classList.remove('active'));

        modal.classList.add('active');
        document.body.style.overflow = 'hidden';
    }
}

function toggleTag(element) {
    element.classList.toggle('active');
}

// Close modal when clicking outside
window.addEventListener('click', function(e) {
    const modal = document.getElementById('submitModModal');
    if (e.target === modal) {
        closeSubmitModal();
    }
});
