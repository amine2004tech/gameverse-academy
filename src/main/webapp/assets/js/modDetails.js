// Cinematic Slider Logic
let currentSlide = 0;
const track = document.getElementById('slidesTrack');
const slides = document.querySelectorAll('.slide-item');
const thumbs = document.querySelectorAll('.thumb-switch-item');

function updateSlider() {
    if (!track || slides.length === 0) return;
    
    // Moving by 100% relative to the track's width (which is 100% of the container)
    // This moves it by exactly one slide width at a time.
    track.style.transform = `translate3d(-${currentSlide * 100}%, 0, 0)`;

    // Update thumbnails
    thumbs.forEach((t, i) => {
        if (i === currentSlide) t.classList.add('active');
        else t.classList.remove('active');
    });
}

function moveSlide(dir) {
    if (slides.length <= 1) return;
    currentSlide = (currentSlide + dir + slides.length) % slides.length;
    updateSlider();
}

function goToSlide(idx) {
    currentSlide = idx;
    updateSlider();
}

// Interactive Stars Logic
const stars = document.querySelectorAll('.star-trigger');
const ratingInput = document.getElementById('ratingInput');

function updateStars(val, isHover = false) {
    stars.forEach((s, idx) => {
        const starDiv = s.querySelector('.selection-star');
        const isActive = idx < val;
        
        if (isActive) {
            starDiv.classList.add('active');
            starDiv.classList.remove('inactive');
        } else {
            starDiv.classList.add('inactive');
            starDiv.classList.remove('active');
        }
    });
}

stars.forEach(star => {
    star.addEventListener('click', () => {
        const val = parseInt(star.dataset.value);
        ratingInput.value = val;
        updateStars(val);
    });

    star.addEventListener('mouseenter', () => {
        const val = parseInt(star.dataset.value);
        updateStars(val, true);
        star.style.transform = 'scale(1.2)';
    });

    star.addEventListener('mouseleave', () => {
        const currentVal = parseInt(ratingInput.value) || 0;
        updateStars(currentVal);
        star.style.transform = 'none';
    });
});

// Auto slider
if (slides && slides.length > 1) {
    setInterval(() => moveSlide(1), 6000);
}
