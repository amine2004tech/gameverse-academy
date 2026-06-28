/* Submit Page JavaScript */

let currentValidFiles = []; // Global array of valid File objects
let unifiedImageOrder = []; // Array of strings: "db:img.jpg" or "new:0"

const MAX_IMAGE_SIZE = 10 * 1024 * 1024; // 10 MB
const MAX_ZIP_SIZE = 500 * 1024 * 1024; // 500 MB
const ALLOWED_IMAGE_TYPES = ['image/jpeg', 'image/png', 'image/webp'];

document.addEventListener('DOMContentLoaded', function() {
    const imageInput = document.getElementById('modImages');
    if (imageInput) {
        imageInput.addEventListener('change', function(e) {
            const hint = this.nextElementSibling;
            
            if (this.files && this.files.length > 0) {
                let newValidFiles = [];
                for(let i=0; i<this.files.length; i++) {
                    const f = this.files[i];
                    let isValid = true;
                    
                    if (f.size === 0) {
                        showToast("Validation Error", `${f.name} was rejected: file is empty.`, "error");
                        isValid = false;
                    } else {
                        const ext = f.name.substring(f.name.lastIndexOf('.')).toLowerCase();
                        const validExts = ['.jpg', '.jpeg', '.png', '.webp'];
                        
                        if (!ALLOWED_IMAGE_TYPES.includes(f.type) && !validExts.includes(ext)) {
                            if (ext === '.svg') {
                                showToast("Validation Error", `${f.name} was rejected: SVG images are not supported.`, "error");
                            } else if (!f.type.startsWith('image/') && f.type !== '') {
                                showToast("Validation Error", `${f.name} was rejected: file content is not a valid image.`, "error");
                            } else {
                                showToast("Validation Error", `${f.name} was rejected: images must be JPG, JPEG, PNG, or WEBP.`, "error");
                            }
                            isValid = false;
                        } else if (f.size > MAX_IMAGE_SIZE) {
                            showToast("Validation Error", `${f.name} was rejected: image size exceeds 10 MB.`, "error");
                            isValid = false;
                        }
                    }
                    
                    if (isValid) {
                        newValidFiles.push(f);
                    }
                }
                
                // Append new valid files to our global tracking array
                currentValidFiles = currentValidFiles.concat(newValidFiles);
                
                // Rebuild the input's FileList so it ONLY contains valid files
                try {
                    const dt = new DataTransfer();
                    currentValidFiles.forEach(file => dt.items.add(file));
                    this.files = dt.files;
                } catch(err) {
                    console.warn("Browser does not support DataTransfer file rebuilding.");
                }
                
                // Add to unifiedImageOrder
                for (let i = 0; i < newValidFiles.length; i++) {
                    unifiedImageOrder.push("new:" + (currentValidFiles.length - newValidFiles.length + i));
                }
                
                renderImagePreviews(this.files);
            }
        });
    }

    const zipInput = document.getElementById('modFile');
    if (zipInput) {
        zipInput.addEventListener('change', function(e) {
            const hint = this.nextElementSibling;
            if (this.files && this.files.length > 0) {
                const f = this.files[0];
                if (f.size === 0) {
                    showToast("Validation Error", `${f.name} was rejected: archive file is empty.`, "error");
                    this.value = '';
                    hint.textContent = "SELECT ZIP";
                    hint.style.color = '';
                    return;
                }
                if (f.size > MAX_ZIP_SIZE) {
                    showToast("Validation Error", `${f.name} was rejected: archive size exceeds 500 MB.`, "error");
                    this.value = '';
                    hint.textContent = "SELECT ZIP";
                    hint.style.color = '';
                    return;
                }
                // Check extension for frontend UX
                const ext = f.name.toLowerCase();
                if (!ext.endsWith('.zip') && !ext.endsWith('.rar') && !ext.endsWith('.7z')) {
                    showToast("Validation Error", `${f.name} was rejected: mod package must be ZIP, RAR, or 7Z.`, "error");
                    this.value = '';
                    hint.textContent = "SELECT ZIP";
                    hint.style.color = '';
                    return;
                }
                
                hint.textContent = f.name;
                hint.style.color = 'var(--accent-light)';
            } else {
                hint.textContent = "SELECT ZIP";
                hint.style.color = '';
            }
        });
    }

    // Form submit listener to gather tags and validate
    const form = document.getElementById('submitModForm');
    if (form) {
        form.addEventListener('submit', function(e) {
            const selectedTagEls = document.querySelectorAll('.tag-selector-item.active');
            const tagsInput = document.getElementById('selectedTags');
            
            const tagIds = Array.from(selectedTagEls).map(el => el.getAttribute('data-tag-id'));
            tagsInput.value = tagIds.join(',');
            
            // Core Text Field Validation (Cinematic Toasts instead of HTML5 bubbles)
            const titleInput = document.getElementById('title');
            if (titleInput && !titleInput.value.trim()) {
                e.preventDefault();
                showToast('Validation Error', 'Missing Field: Title is strictly required.', 'error');
                titleInput.focus();
                return false;
            }
            
            const gameInput = document.getElementById('gameId');
            if (gameInput && !gameInput.value) {
                e.preventDefault();
                showToast('Validation Error', 'Missing Field: Please assign this mod to a Game Universe.', 'error');
                gameInput.focus();
                return false;
            }

            // Frontend Validation: Require Image
            const isNew = !document.getElementById('modId').value;
            const imgInput = document.getElementById('modImages');
            
            if (isNew) {
                if (unifiedImageOrder.length === 0) {
                    e.preventDefault();
                    showToast('Submission Blocked', 'Missing Media: At least one image is required to build the mod gallery.', 'error');
                    return false;
                }
                
                const zipInput = document.getElementById('modFile');
                if (!zipInput.files || zipInput.files.length === 0) {
                    e.preventDefault();
                    showToast('Submission Blocked', 'Missing Archive: A mod archive package (ZIP/RAR/7Z) is required.', 'error');
                    return false;
                }
            }
            
            if (unifiedImageOrder.length === 0) {
                e.preventDefault();
                showToast('Submission Blocked', 'You removed all images. Please select at least one image.', 'error');
                return false;
            }
            
            // If validation passes, use AJAX to track upload progress
            e.preventDefault();
            
            const formData = new FormData(form);
            const xhr = new XMLHttpRequest();
            xhr.open('POST', form.action, true);
            
            closeSubmitModal();
            
            let submitGrid = document.querySelector('.mod-grid');
            if (!submitGrid) {
                const emptyState = document.querySelector('.empty-mods-state');
                if (emptyState) {
                    emptyState.remove();
                    submitGrid = document.createElement('div');
                    submitGrid.className = 'mod-grid';
                    const section = document.querySelector('.published-mods-section');
                    if (section) section.appendChild(submitGrid);
                }
            }
            const contextPath = document.body.dataset.contextPath || '/gameverseacademy';
            
            const continueWithThumbnail = (resolvedThumbSrc) => {
                let targetCard = null;
                
                if (!isNew) {
                    const modIdVal = document.getElementById('modId').value;
                    targetCard = document.getElementById('mod-card-' + modIdVal);
                    if (targetCard) {
                        targetCard.classList.add('uploading');
                        
                        const titleNode = targetCard.querySelector('.mod-card-title');
                        if (titleNode && titleInput.value) {
                            titleNode.textContent = titleInput.value;
                        }
                        
                        if (resolvedThumbSrc) {
                            const imgNode = targetCard.querySelector('.mod-thumbnail');
                            if (imgNode) {
                                imgNode.src = resolvedThumbSrc;
                            }
                        }
                        
                        const imgWrapper = targetCard.querySelector('.mod-thumbnail-wrapper');
                        if (imgWrapper && !imgWrapper.querySelector('.upload-progress-bar-container')) {
                            imgWrapper.insertAdjacentHTML('beforeend', `
                                <div class="upload-progress-overlay"></div>
                                <div class="upload-progress-bar-container">
                                    <div class="upload-progress-bar" style="width: 0%"></div>
                                </div>
                            `);
                        }
                    }
                } else {
                    targetCard = document.createElement('div');
                    targetCard.className = 'mod-card gv-card uploading temp-upload-card';
                    
                    targetCard.innerHTML = `
                        <div class="mod-thumbnail-wrapper">
                            <img src="${resolvedThumbSrc}" class="mod-thumbnail" alt="Uploading..." style="object-fit: cover;" />
                            <div class="upload-progress-overlay"></div>
                            <div class="upload-progress-bar-container">
                                <div class="upload-progress-bar" style="width: 0%"></div>
                            </div>
                        </div>
                        <div class="mod-card-content">
                            <h3 class="mod-card-title">${titleInput.value}</h3>
                            <p class="mod-card-description" style="color:var(--text-muted);font-size:0.8rem;">Uploading...</p>
                        </div>
                    `;
                }

                xhr.upload.onprogress = function(event) {
                    if (event.lengthComputable) {
                        const percentComplete = Math.round((event.loaded / event.total) * 100);
                        if (targetCard) {
                            const bar = targetCard.querySelector('.upload-progress-bar');
                            if (bar) bar.style.width = percentComplete + '%';
                        }
                    }
                };
                
                xhr.onload = function() {
                    if (xhr.status >= 200 && xhr.status < 400) {
                        window.location.reload();
                    } else {
                        showToast('Upload Failed', 'There was a problem submitting the mod.', 'error');
                        if (targetCard) {
                            targetCard.classList.remove('uploading');
                            const overlay = targetCard.querySelector('.upload-progress-overlay');
                            const pContainer = targetCard.querySelector('.upload-progress-bar-container');
                            if(overlay) overlay.remove();
                            if(pContainer) pContainer.remove();
                            if (isNew && targetCard.parentNode) {
                                targetCard.parentNode.removeChild(targetCard);
                            }
                        }
                    }
                };
                
                xhr.onerror = function() {
                    showToast('Upload Failed', 'Network error occurred.', 'error');
                    if (targetCard) {
                        targetCard.classList.remove('uploading');
                        if (isNew && targetCard.parentNode) {
                            targetCard.parentNode.removeChild(targetCard);
                        }
                    }
                };
                
                const startUpload = () => {
                    if (isNew && submitGrid && targetCard) {
                        if (submitGrid.firstChild) {
                            submitGrid.insertBefore(targetCard, submitGrid.firstChild);
                        } else {
                            submitGrid.appendChild(targetCard);
                        }
                    }
                    
                    setTimeout(() => {
                        xhr.send(formData);
                    }, 50);
                };
    
                if (targetCard) {
                    const imgNode = targetCard.querySelector('.mod-thumbnail');
                    if (imgNode && imgNode.src && imgNode.src.startsWith('data:')) {
                        const tempImg = new Image();
                        tempImg.onload = startUpload;
                        tempImg.onerror = startUpload;
                        tempImg.src = imgNode.src;
                    } else {
                        startUpload();
                    }
                } else {
                    startUpload();
                }
            };
            
            if (unifiedImageOrder.length > 0) {
                if (unifiedImageOrder[0].startsWith('new:')) {
                    const idx = parseInt(unifiedImageOrder[0].substring(4));
                    const file = currentValidFiles[idx];
                    if (file) {
                        const reader = new FileReader();
                        reader.onload = (ev) => continueWithThumbnail(ev.target.result);
                        reader.onerror = () => continueWithThumbnail('');
                        reader.readAsDataURL(file);
                        return;
                    }
                } else if (unifiedImageOrder[0].startsWith('db:')) {
                    continueWithThumbnail(contextPath + '/assets/images/mods/' + unifiedImageOrder[0].substring(3));
                    return;
                }
            }
            
            continueWithThumbnail('');
        });
    }
    
    // Initial render for empty state
    renderImagePreviews(document.getElementById('modImages') ? document.getElementById('modImages').files : []);
});

function renderImagePreviews(files) {
    const container = document.getElementById('imagePreviewContainer');
    if (!container) return;
    
    container.innerHTML = '';
    const hint = document.getElementById('modImages').nextElementSibling;
    const totalCount = unifiedImageOrder.length;
    
    if (totalCount > 0) {
        hint.textContent = totalCount + " IMAGES SELECTED";
        hint.style.color = 'var(--accent-light)';
    } else {
        hint.textContent = "SELECT IMAGES";
        hint.style.color = '';
        container.innerHTML = '<div class="gv-slider-empty">Add at least one image to build the mod gallery.</div>';
    }
    
    const contextPath = document.body.dataset.contextPath || '/gameverseacademy';
    
    unifiedImageOrder.forEach((token, currentPos) => {
        const card = document.createElement('div');
        card.className = 'gv-slider-card';
        
        card.draggable = true;
        card.ondragstart = (e) => {
            e.dataTransfer.setData("text/plain", currentPos);
            e.dataTransfer.effectAllowed = "move";
            card.classList.add('dragging');
        };
        card.ondragend = () => card.classList.remove('dragging');
        card.ondragover = (e) => { e.preventDefault(); e.dataTransfer.dropEffect = "move"; };
        card.ondragenter = (e) => { e.preventDefault(); card.classList.add('drag-over'); };
        card.ondragleave = (e) => { card.classList.remove('drag-over'); };
        card.ondrop = (e) => {
            e.preventDefault();
            card.classList.remove('drag-over');
            const fromIdx = parseInt(e.dataTransfer.getData("text/plain"));
            if (fromIdx !== currentPos && !isNaN(fromIdx)) {
                const element = unifiedImageOrder.splice(fromIdx, 1)[0];
                unifiedImageOrder.splice(currentPos, 0, element);
                renderImagePreviews(files);
            }
        };
        
        const img = document.createElement('img');
        img.className = 'preview-img';
        
        const badge = document.createElement('div');
        badge.className = 'gv-slider-badge';
        
        if (currentPos === 0) {
            const starIcon = document.createElement('img');
            starIcon.src = contextPath + '/assets/images/icons/star active dark mode.png';
            const textSpan = document.createElement('span');
            textSpan.textContent = 'Thumbnail';
            badge.appendChild(starIcon);
            badge.appendChild(textSpan);
        } else {
            if (token.startsWith("db:")) {
                badge.innerHTML = `<span style="color:#aaa;">DB RECORD</span>`;
            } else {
                badge.innerHTML = `<span style="color:#5cffb0;">NEW</span>`;
            }
        }
        
        if (token.startsWith("db:")) {
            img.src = contextPath + '/assets/images/mods/' + token.substring(3);
        } else {
            const fileIdx = parseInt(token.substring(4));
            const file = files[fileIdx];
            if (file) {
                const reader = new FileReader();
                reader.onload = (e) => img.src = e.target.result;
                reader.readAsDataURL(file);
            }
        }
        
        card.appendChild(img);
        card.appendChild(badge);
        
        const removeBtn = document.createElement('div');
        removeBtn.className = 'gv-slider-remove';
        removeBtn.innerHTML = '&times;';
        removeBtn.onclick = (e) => {
            e.stopPropagation();
            const removedToken = unifiedImageOrder.splice(currentPos, 1)[0];
            
            if (removedToken.startsWith("new:")) {
                const removedIdx = parseInt(removedToken.substring(4));
                currentValidFiles.splice(removedIdx, 1);
                
                for (let i = 0; i < unifiedImageOrder.length; i++) {
                    if (unifiedImageOrder[i].startsWith("new:")) {
                        const idx = parseInt(unifiedImageOrder[i].substring(4));
                        if (idx > removedIdx) {
                            unifiedImageOrder[i] = "new:" + (idx - 1);
                        }
                    }
                }
                
                const dt = new DataTransfer();
                currentValidFiles.forEach(f => dt.items.add(f));
                document.getElementById('modImages').files = dt.files;
            }
            
            renderImagePreviews(document.getElementById('modImages').files);
        };
        
        card.appendChild(removeBtn);
        container.appendChild(card);
    });
    
    const orderInput = document.getElementById('imageOrder');
    if (orderInput) orderInput.value = unifiedImageOrder.join(',');
}

function openSubmitModal() {
    const pendingCount = parseInt(document.body.getAttribute('data-pending-count') || '0', 10);
    if (pendingCount >= 2) {
        showToast("Access Denied", "You cannot have more than 2 pending mod submissions at a time.", "error");
        return;
    }
    const modal = document.getElementById('submitModModal');
    if (modal) {
        document.getElementById('submitModForm').reset();
        document.getElementById('modId').value = "";
        document.getElementById('modalTitle').textContent = "ARCHIVE NEW ENTRY";
        
        document.querySelectorAll('.tag-selector-item').forEach(el => el.classList.remove('active'));
        
        document.querySelectorAll('.file-upload-hint').forEach(el => {
            el.textContent = el.parentElement.querySelector('input').accept.includes('.zip') ? "SELECT ZIP" : "SELECT IMAGES";
            el.style.color = '';
        });
        
        currentValidFiles = [];
        unifiedImageOrder = [];
        document.getElementById('imageOrder').value = '';
        renderImagePreviews([]);

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

function openEditModalFromBtn(btn) {
    const id = btn.getAttribute('data-id');
    const title = btn.getAttribute('data-title');
    const description = btn.getAttribute('data-desc');
    const gameId = btn.getAttribute('data-game');
    const youtubeUrl = btn.getAttribute('data-yt');
    const zipName = btn.getAttribute('data-zip');
    let images = [];
    try {
        images = JSON.parse(btn.getAttribute('data-images'));
    } catch(e) {}

    let tags = [];
    try {
        tags = JSON.parse(btn.getAttribute('data-tags') || '[]');
    } catch(e) {}

    const modal = document.getElementById('submitModModal');
    if (modal) {
        document.getElementById('modalTitle').textContent = "EDIT ARCHIVE ENTRY";
        document.getElementById('modId').value = id;
        document.getElementById('title').value = title;
        document.getElementById('description').value = description;
        document.getElementById('gameId').value = gameId;
        
        if (youtubeUrl && youtubeUrl !== "null") {
            document.getElementById('youtubeUrl').value = youtubeUrl;
        } else {
            document.getElementById('youtubeUrl').value = "";
        }
        
        document.querySelectorAll('.tag-selector-item').forEach(el => {
            const tagId = parseInt(el.getAttribute('data-tag-id'));
            if (tags.includes(tagId)) {
                el.classList.add('active');
            } else {
                el.classList.remove('active');
            }
        });
        
        const zipHint = document.querySelector('#modFile').parentElement.querySelector('.file-upload-hint');
        if (zipName && zipName !== "null" && zipName !== "") {
            zipHint.textContent = zipName;
            zipHint.style.color = "#5cffb0";
        } else {
            zipHint.textContent = "SELECT ZIP";
            zipHint.style.color = "";
        }
        
        currentValidFiles = [];
        unifiedImageOrder = images.map(img => "db:" + img);
        document.getElementById('imageOrder').value = '';
        renderImagePreviews([]);

        modal.classList.add('active');
        document.body.style.overflow = 'hidden';
    }
}

function toggleTag(element) {
    element.classList.toggle('active');
}

window.addEventListener('click', function(e) {
    const modal = document.getElementById('submitModModal');
    if (e.target === modal) {
        closeSubmitModal();
    }
});
