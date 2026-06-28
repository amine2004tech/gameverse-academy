/**
 * Generic Utility JavaScript for GameVerse Academy
 */

document.addEventListener('DOMContentLoaded', () => {
    initModals();
    initAvatarSelector();
});

/**
 * Initializes generic modal open/close functionality
 */
function initModals() {
    const modalCloseBtns = document.querySelectorAll('.modal-close, .modal-cancel');
    
    modalCloseBtns.forEach(btn => {
        btn.addEventListener('click', (e) => {
            const modal = e.target.closest('.modal-overlay');
            if (modal) {
                closeModal(modal.id);
            }
        });
    });

    // Close on clicking outside the modal content
    const overlays = document.querySelectorAll('.modal-overlay');
    overlays.forEach(overlay => {
        overlay.addEventListener('click', (e) => {
            if (e.target === overlay) {
                closeModal(overlay.id);
            }
        });
    });
}

function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.add('active');
        document.body.style.overflow = 'hidden'; // Prevent background scrolling
    }
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.remove('active');
        document.body.style.overflow = '';
    }
}

/**
 * Initializes the avatar selection system
 */
function initAvatarSelector() {
    const avatarTrigger = document.getElementById('avatarSelectorTrigger');
    const avatarModal = document.getElementById('avatarModal');
    
    if (avatarTrigger && avatarModal) {
        const colorDots = document.querySelectorAll('.color-dot');
        const avatarGrid = document.getElementById('avatarGrid');
        const avatarInput = document.getElementById('selectedAvatarInput');
        const contextPath = document.body.dataset.contextPath || '';

        // Internal function to load avatars for a specific color
        window.loadAvatars = function(color) {
            if (!avatarGrid) return;
            
            // Highlight active dot
            colorDots.forEach(dot => {
                if (dot.dataset.color === color) {
                    dot.classList.add('active');
                } else {
                    dot.classList.remove('active');
                }
            });

            // Clear existing avatars
            avatarGrid.innerHTML = '';
            
            // Generate 12 standard avatars
            for (let i = 1; i <= 12; i++) {
                const imgContainer = document.createElement('div');
                imgContainer.className = 'avatar-option';
                
                const img = document.createElement('img');
                const imgPath = `${contextPath}/assets/images/avatars_default/${color}/${i}.png`;
                const relativePath = `assets/images/avatars_default/${color}/${i}.png`;
                
                img.src = imgPath;
                img.alt = `Avatar ${i}`;
                
                img.onerror = function() {
                    imgContainer.style.display = 'none';
                };
                
                imgContainer.addEventListener('click', () => {
                    avatarTrigger.src = imgPath;
                    if (avatarInput) avatarInput.value = relativePath;
                    closeModal('avatarModal');
                    const avatarForm = document.getElementById('avatarUpdateForm');
                    if (avatarForm) avatarForm.submit();
                });

                imgContainer.appendChild(img);
                avatarGrid.appendChild(imgContainer);
            }
        };

        // Attach click listeners to dots
        colorDots.forEach(dot => {
            dot.addEventListener('click', () => {
                loadAvatars(dot.dataset.color);
            });
        });

        // MANDATORY: Load "blue" by default immediately
        loadAvatars('blue');

        // Observer to ensure it's loaded when modal opens (extra safety)
        const observer = new MutationObserver((mutations) => {
            mutations.forEach((mutation) => {
                if (mutation.target.classList.contains('active')) {
                    const activeColor = document.querySelector('.color-dot.active');
                    if (activeColor) {
                        loadAvatars(activeColor.dataset.color);
                    } else {
                        loadAvatars('blue');
                    }
                }
            });
        });
        
        observer.observe(avatarModal, { attributes: true, attributeFilter: ['class'] });
    }
}

/**
 * Global Cinematic Toast Notification System
 * @param {string} title - Title of the toast
 * @param {string} message - Message body
 * @param {string} type - 'error', 'success', 'info', 'warning'
 */
function showToast(title, message, type = 'info') {
    let container = document.getElementById('gv-toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'gv-toast-container';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `gv-toast ${type}`;

    const content = document.createElement('div');
    content.className = 'gv-toast-content';

    const titleEl = document.createElement('div');
    titleEl.className = 'gv-toast-title';
    titleEl.innerText = title;

    const messageEl = document.createElement('div');
    messageEl.className = 'gv-toast-message';
    messageEl.innerHTML = message;

    content.appendChild(titleEl);
    content.appendChild(messageEl);

    const closeBtn = document.createElement('button');
    closeBtn.className = 'gv-toast-close';
    closeBtn.innerHTML = '&times;';
    closeBtn.onclick = () => {
        toast.classList.add('closing');
        setTimeout(() => toast.remove(), 300);
    };

    toast.appendChild(content);
    toast.appendChild(closeBtn);

    container.appendChild(toast);

    // Auto dismiss after 5 seconds
    setTimeout(() => {
        if (document.body.contains(toast)) {
            toast.classList.add('closing');
            setTimeout(() => toast.remove(), 300);
        }
    }, 5000);
}

