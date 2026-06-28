/**
 * Registration Page Script for GameVerse Academy
 */
document.addEventListener('DOMContentLoaded', () => {
    // Modify the profile modal form so it doesn't auto-submit and conflict
    const modalForm = document.getElementById('avatarUpdateForm');
    if (modalForm) {
        modalForm.id = 'avatarUpdateFormDisabled';
        const modalInput = modalForm.querySelector('#selectedAvatarInput');
        if (modalInput) {
            modalInput.id = 'selectedAvatarInputDisabled';
        }
    }
});
