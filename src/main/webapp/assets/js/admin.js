document.addEventListener("DOMContentLoaded", () => {
    const tagLinks = document.querySelectorAll('.tag-filter-link');
    const gameCards = document.querySelectorAll('.game-card');

    function performAdminFilter() {
        const activeTags = Array.from(document.querySelectorAll('.mod-tag-ticket.active'))
            .map(ticket => ticket.closest('a').dataset.tagId);

        const activeGameCard = document.querySelector('.game-card.active');
        const gameId = activeGameCard ? activeGameCard.dataset.gameId : '';

        const params = new URLSearchParams();
        if(gameId) params.append('game', gameId);
        if(activeTags.length > 0) params.append('tags', activeTags.join(','));

        const query = params.toString();
        // Since we are in an external file, we need to extract the context path
        // We'll assume the URL ends with /AdminController or similar
        const ctx = window.location.pathname.substring(0, window.location.pathname.indexOf('/', 1));
        window.location.href = ctx + '/AdminController' + (query ? '?' + query : '');
    }

    tagLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const ticket = link.querySelector('.mod-tag-ticket');
            ticket.classList.toggle('active');
            performAdminFilter();
        });
    });

    gameCards.forEach(card => {
        card.addEventListener('click', (e) => {
            e.preventDefault();
            if(card.classList.contains('active')) {
                card.classList.remove('active');
            } else {
                gameCards.forEach(c => c.classList.remove('active'));
                card.classList.add('active');
            }
            performAdminFilter();
        });
    });
});
