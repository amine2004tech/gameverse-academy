/**
 * Navbar Interactivity for GameVerse Academy
 */

document.addEventListener('DOMContentLoaded', function() {
    const mobileToggle = document.querySelector('.gv-mobile-toggle');
    const navCenter = document.querySelector('.gv-navbar-center');
    const themeSwitch = document.querySelector('.theme-switch');
    const body = document.body;

    // Mobile Menu Toggle
    if (mobileToggle) {
        mobileToggle.addEventListener('click', () => {
            navCenter.classList.toggle('active');
            
            // Animate toggle bars (optional)
            const spans = mobileToggle.querySelectorAll('span');
            spans.forEach((span, index) => {
                if (navCenter.classList.contains('active')) {
                    if (index === 0) span.style.transform = 'rotate(45deg) translate(5px, 5px)';
                    if (index === 1) span.style.opacity = '0';
                    if (index === 2) span.style.transform = 'rotate(-45deg) translate(5px, -5px)';
                } else {
                    span.style.transform = 'none';
                    span.style.opacity = '1';
                }
            });
        });
    }

    // Theme Switcher Logic
    const currentTheme = localStorage.getItem('gv-theme');
    if (currentTheme === 'light') {
        body.classList.add('light-mode');
    }

    if (themeSwitch) {
        themeSwitch.addEventListener('click', () => {
            body.classList.toggle('light-mode');
            const theme = body.classList.contains('light-mode') ? 'light' : 'dark';
            localStorage.setItem('gv-theme', theme);
        });
    }

    // Sticky Navbar reveal on scroll (optional polish)
    let lastScroll = 0;
    window.addEventListener('scroll', () => {
        const currentScroll = window.pageYOffset;
        const navbar = document.querySelector('.gv-navbar');
        
        if (currentScroll > 50) {
            navbar.classList.add('scrolled');
        } else {
            navbar.classList.remove('scrolled');
        }
        
        lastScroll = currentScroll;
    });
});
