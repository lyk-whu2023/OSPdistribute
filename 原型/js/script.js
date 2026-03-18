// 轮播图功能
function initCarousel() {
    const carouselItems = document.querySelectorAll('.carousel-item');
    let currentIndex = 0;
    
    function showNextSlide() {
        carouselItems[currentIndex].classList.remove('active');
        currentIndex = (currentIndex + 1) % carouselItems.length;
        carouselItems[currentIndex].classList.add('active');
    }
    
    // 自动轮播
    setInterval(showNextSlide, 3000);
}

// 秒杀倒计时功能
function initSeckillCountdown() {
    const countdownElements = document.querySelectorAll('.time-countdown span');
    let hours = 1;
    let minutes = 30;
    let seconds = 45;
    
    function updateCountdown() {
        if (seconds === 0) {
            if (minutes === 0) {
                if (hours === 0) {
                    // 倒计时结束
                    return;
                } else {
                    hours--;
                    minutes = 59;
                    seconds = 59;
                }
            } else {
                minutes--;
                seconds = 59;
            }
        } else {
            seconds--;
        }
        
        // 更新显示
        countdownElements[0].textContent = hours.toString().padStart(2, '0');
        countdownElements[1].textContent = minutes.toString().padStart(2, '0');
        countdownElements[2].textContent = seconds.toString().padStart(2, '0');
    }
    
    // 每秒更新一次
    setInterval(updateCountdown, 1000);
}

// 加入购物车功能
function initAddToCart() {
    const addToCartButtons = document.querySelectorAll('.add-to-cart');
    
    addToCartButtons.forEach(button => {
        button.addEventListener('click', function() {
            const productName = this.parentElement.querySelector('h3').textContent;
            alert(`已将 ${productName} 加入购物车`);
        });
    });
}

// 秒杀按钮功能
function initSeckillButton() {
    const seckillButtons = document.querySelectorAll('.seckill-btn');
    
    seckillButtons.forEach(button => {
        button.addEventListener('click', function() {
            const productName = this.parentElement.querySelector('h3').textContent;
            alert(`正在抢购 ${productName}，请稍候...`);
        });
    });
}

// 关注店铺功能
function initFollowStore() {
    const followButtons = document.querySelectorAll('.follow-store');
    
    followButtons.forEach(button => {
        button.addEventListener('click', function() {
            const storeName = this.parentElement.querySelector('h3').textContent;
            if (this.textContent === '关注') {
                this.textContent = '已关注';
                this.style.backgroundColor = '#999';
                alert(`已关注 ${storeName}`);
            } else {
                this.textContent = '关注';
                this.style.backgroundColor = '#4caf50';
                alert(`已取消关注 ${storeName}`);
            }
        });
    });
}

// 搜索功能
function initSearch() {
    const searchButton = document.querySelector('.search-box button');
    const searchInput = document.querySelector('.search-box input');
    
    searchButton.addEventListener('click', function() {
        const keyword = searchInput.value.trim();
        if (keyword) {
            alert(`正在搜索 "${keyword}"`);
        } else {
            alert('请输入搜索关键词');
        }
    });
    
    // 按回车键搜索
    searchInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            searchButton.click();
        }
    });
}

// 页面加载完成后初始化所有功能
window.addEventListener('DOMContentLoaded', function() {
    initCarousel();
    initSeckillCountdown();
    initAddToCart();
    initSeckillButton();
    initFollowStore();
    initSearch();
});