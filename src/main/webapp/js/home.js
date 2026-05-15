/* GameVerse Academy - Home Page Scripts */

document.addEventListener('DOMContentLoaded', () => {
    initFilters();
    initModCards();
    applyEntranceAnimations();
});

/**
 * Handles filter selection and URL parameter management.
 * Ensures that game and tag filters can be combined.
 */
function initFilters() {
    const filterLinks = document.querySelectorAll('.tag-filter-link, .game-card');
    
    filterLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            const tagId = link.getAttribute('data-tag-id');
            const gameId = link.getAttribute('data-game-id');
            
            // If it's a reset button (no specific IDs), let the default href work
            if (!tagId && !gameId) return;
            
            e.preventDefault();
            
            const url = new URL(window.location.href);
            const params = new URLSearchParams(url.search);
            
            if (tagId) {
                // Handle multiple tags
                let currentTags = params.get('tags') ? params.get('tags').split(',') : [];
                
                if (currentTags.includes(tagId)) {
                    currentTags = currentTags.filter(id => id !== tagId);
                } else {
                    currentTags.push(tagId);
                }
                
                if (currentTags.length > 0) {
                    params.set('tags', currentTags.join(','));
                } else {
                    params.delete('tags');
                }
            }
            
            if (gameId) {
                // Toggle game filter (only one allowed)
                if (params.get('game') === gameId) {
                    params.delete('game');
                } else {
                    params.set('game', gameId);
                }
            }
            
            // Maintain other params if any, then navigate
            window.location.search = params.toString();
        });
    });
}

/**
 * Handles mod card interactions.
 */
function initModCards() {
    const cards = document.querySelectorAll('.mod-card');
    
    cards.forEach(card => {
        card.addEventListener('mouseenter', () => {
            // Subtle sound effect could go here if allowed
        });
        
        // The card is already a <a> tag, so default behavior works
    });
}

/**
 * Applies entrance animations to elements as they enter the viewport.
 */
function applyEntranceAnimations() {
    const observerOptions = {
        threshold: 0.1
    };
    
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('animate-in');
                observer.unobserve(entry.target);
            }
        });
    }, observerOptions);
    
    // Animate filter sections
    document.querySelectorAll('.filter-section').forEach((el, i) => {
        el.style.opacity = '0';
        el.classList.add(`delay-${(i % 3) + 1}`);
        observer.observe(el);
    });
    
    // Animate mod cards
    document.querySelectorAll('.mod-card').forEach((el, i) => {
        el.style.opacity = '0';
        el.classList.add(`delay-${(i % 5) + 1}`);
        observer.observe(el);
    });
}
