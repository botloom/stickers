class DemoAnimation {
    constructor() {
        this.stickerEmojis = ['😀', '😂', '🤣', '😊', '😍', '🥰', '😎', '🤔', '😴', '🤯', '😱', '🥳', '😈', '👻', '💀', '🤖', '🎉', '🎊', '🎁', '💖', '💔', '💯', '✨', '🔥', '💥', '🌈', '🍕', '🍔', '🍦', '🎵', '🎶', '🎸', '🎮', '🏆', '🚀', '🌟', '💫', '⭐', '🌙', '☀️'];
        this.stickerColors = ['#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4', '#FFEAA7', '#DDA0DD', '#98D8C8', '#F7DC6F', '#BB8FCE', '#85C1E9'];
        this.cursorIndicator = document.getElementById('cursorIndicator');
        this.init();
    }

    init() {
        this.bindEvents();
        this.sidebarCollapsed = false;
        this.toggleSidebar();
        setTimeout(() => this.startDemo(), 1000);
    }

    bindEvents() {
        document.getElementById('searchBtn').addEventListener('click', () => this.performSearch());
        document.getElementById('searchInput').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') this.performSearch();
        });
        
        document.getElementById('clearBtn').addEventListener('click', () => this.clearSearch());
        
        document.getElementById('sidebar-home').addEventListener('click', () => this.switchToHome());
        document.getElementById('sidebar-favorites').addEventListener('click', () => this.switchToFavorites());
        document.getElementById('sidebar-settings').addEventListener('click', () => this.switchToSettings());
        
        document.getElementById('saveSettingsBtn').addEventListener('click', () => this.saveSettings());
        document.getElementById('resetSettingsBtn').addEventListener('click', () => this.resetSettings());
        
        document.querySelector('.menu-btn').addEventListener('click', () => this.toggleSidebar());
        
        document.querySelectorAll('.card').forEach(card => {
            card.addEventListener('click', () => this.downloadSticker(card));
        });
    }

    toggleSidebar() {
        const sidebar = document.querySelector('.sidebar');
        this.sidebarCollapsed = !this.sidebarCollapsed;
        
        if (this.sidebarCollapsed) {
            sidebar.classList.add('collapsed');
        } else {
            sidebar.classList.remove('collapsed');
        }
    }

    async startDemo() {
        await this.step1_OpenApp();
        await this.delay(1000);
        await this.step2_Search();
        await this.delay(2000);
        await this.step3_ViewResults();
        await this.delay(2000);
        await this.step4_Download();
        await this.delay(2000);
        await this.step5_Favorites();
        await this.delay(2000);
        await this.step6_Settings();
        await this.delay(2000);
        await this.switchToHome();
        await this.delay(1000);
        this.startDemo();
    }

    delay(ms) {
        return new Promise(resolve => setTimeout(resolve, ms));
    }

    showCursorAt(element, offsetX = 0.5, offsetY = 0.5) {
        const rect = element.getBoundingClientRect();
        const x = rect.left + rect.width * offsetX;
        const y = rect.top + rect.height * offsetY;
        
        this.cursorIndicator.style.left = `${x}px`;
        this.cursorIndicator.style.top = `${y}px`;
        this.cursorIndicator.classList.add('active');
    }

    hideCursor() {
        this.cursorIndicator.classList.remove('active');
    }

    async simulateClick(element, offsetX = 0.5, offsetY = 0.5) {
        this.showCursorAt(element, offsetX, offsetY);
        await this.delay(300);
        this.cursorIndicator.classList.add('clicking');
        await this.delay(150);
        this.cursorIndicator.classList.remove('clicking');
        await this.delay(200);
    }

    async step1_OpenApp() {
        this.updateStatus('应用已启动', 'success');
        await this.animateElement('.app-icon', 'bounce');
    }

    async step2_Search() {
        const searchInput = document.getElementById('searchInput');
        const searchBtn = document.getElementById('searchBtn');
        const searchTerm = '开心';
        
        this.updateStatus('正在搜索...', 'loading');
        
        await this.simulateClick(searchInput, 0.1, 0.5);
        
        for (let i = 0; i <= searchTerm.length; i++) {
            searchInput.value = searchTerm.substring(0, i);
            await this.delay(100);
        }
        
        await this.delay(500);
        await this.simulateClick(searchBtn);
        this.performSearch();
    }

    async step3_ViewResults() {
        await this.delay(1000);
        this.updateStatus(`找到 ${document.querySelectorAll('.card').length} 个表情包`, 'success');
        
        const cards = document.querySelectorAll('.card');
        for (let i = 0; i < Math.min(3, cards.length); i++) {
            await this.simulateClick(cards[i], 0.5, 0.5);
            await this.delay(300);
        }
    }

    async step4_Download() {
        const cards = document.querySelectorAll('.card');
        if (cards.length > 0) {
            this.updateStatus('正在下载...', 'loading');
            await this.simulateClick(cards[0], 0.5, 0.5);
            await this.downloadSticker(cards[0]);
        }
    }

    async step5_Favorites() {
        const sidebarFavorites = document.getElementById('sidebar-favorites');
        await this.simulateClick(sidebarFavorites, 0.5, 0.5);
        
        this.switchToFavorites();
        await this.delay(500);
        
        this.updateStatus('查看收藏', 'success');
        
        const cards = document.querySelectorAll('#favoritesGrid .card');
        for (let i = 0; i < Math.min(2, cards.length); i++) {
            await this.simulateClick(cards[i], 0.5, 0.5);
            await this.delay(300);
        }
    }

    async step6_Settings() {
        const menuBtn = document.querySelector('.menu-btn');
        await this.simulateClick(menuBtn, 0.5, 0.5);
        this.toggleSidebar();
        await this.delay(300);
        
        const sidebarSettings = document.getElementById('sidebar-settings');
        await this.simulateClick(sidebarSettings, 0.5, 0.5);
        
        this.switchToSettings();
        await this.delay(500);
        
        this.updateStatus('配置数据源', 'loading');
        
        const douyinToggle = document.getElementById('douyinToggle');
        await this.simulateClick(douyinToggle, 0.5, 0.5);
        douyinToggle.checked = true;
        await this.delay(800);
        
        this.updateStatus('配置完成', 'success');
    }

    performSearch() {
        const searchInput = document.getElementById('searchInput');
        const searchTerm = searchInput.value.trim();
        
        if (!searchTerm) return;
        
        this.updateStatus('正在搜索...', 'loading');
        
        const searchBox = document.querySelector('.search-box');
        searchBox.style.transform = 'scale(0.98)';
        setTimeout(() => {
            searchBox.style.transform = 'scale(1)';
        }, 100);
        
        const gridContainer = document.getElementById('gridContainer');
        gridContainer.innerHTML = '';
        
        const homePage = document.getElementById('homePage');
        homePage.classList.add('has-results');
        
        setTimeout(() => {
            this.loadStickers(searchTerm);
            this.updateStatus(`找到 ${Math.floor(Math.random() * 10) + 8} 个表情包`, 'success');
        }, 800);
    }

    clearSearch() {
        const searchInput = document.getElementById('searchInput');
        const gridContainer = document.getElementById('gridContainer');
        const homePage = document.getElementById('homePage');
        
        searchInput.value = '';
        gridContainer.innerHTML = '';
        homePage.classList.remove('has-results');
        
        this.updateStatus('已清除', 'success');
    }

    saveSettings() {
        this.updateStatus('保存设置...', 'loading');
        setTimeout(() => {
            this.updateStatus('设置已保存', 'success');
        }, 500);
    }

    resetSettings() {
        this.updateStatus('恢复默认...', 'loading');
        setTimeout(() => {
            document.getElementById('chineseBqbToggle').checked = true;
            document.getElementById('douyinToggle').checked = false;
            document.getElementById('wechatToggle').checked = false;
            this.updateStatus('已恢复默认设置', 'success');
        }, 500);
    }

    loadFavorites() {
        const favoritesGrid = document.getElementById('favoritesGrid');
        favoritesGrid.innerHTML = '';
        
        const count = 6 + Math.floor(Math.random() * 5);
        const shuffledEmojis = [...this.stickerEmojis].sort(() => Math.random() - 0.5);
        
        for (let i = 0; i < count; i++) {
            const emoji = shuffledEmojis[i % shuffledEmojis.length];
            const color = this.stickerColors[Math.floor(Math.random() * this.stickerColors.length)];
            const card = this.createStickerCard(emoji, color, i);
            favoritesGrid.appendChild(card);
            
            setTimeout(() => {
                card.classList.add('visible');
            }, i * 100);
        }
    }

    loadStickers(keyword) {
        const gridContainer = document.getElementById('gridContainer');
        gridContainer.innerHTML = '';
        
        const count = 8 + Math.floor(Math.random() * 6);
        const shuffledEmojis = [...this.stickerEmojis].sort(() => Math.random() - 0.5);
        
        for (let i = 0; i < count; i++) {
            const emoji = shuffledEmojis[i % shuffledEmojis.length];
            const color = this.stickerColors[Math.floor(Math.random() * this.stickerColors.length)];
            const card = this.createStickerCard(emoji, color, i);
            gridContainer.appendChild(card);
            
            setTimeout(() => {
                card.classList.add('visible');
            }, i * 100);
        }
    }

    createStickerCard(emoji, color, index) {
        const card = document.createElement('div');
        card.className = 'card';
        
        const isGif = Math.random() > 0.7;
        
        const content = document.createElement('div');
        content.className = 'card-content';
        content.style.cssText = `
            display: flex;
            align-items: center;
            justify-content: center;
            width: 100%;
            height: 100%;
            background: ${color}20;
        `;
        content.textContent = emoji;
        
        card.appendChild(content);
        
        if (isGif) {
            const gifDot = document.createElement('div');
            gifDot.className = 'gif-dot';
            card.appendChild(gifDot);
        }
        
        card.addEventListener('click', () => this.downloadSticker(card));
        
        return card;
    }

    async downloadSticker(card) {
        card.style.transform = 'scale(0.9)';
        await this.delay(100);
        card.style.transform = 'scale(1.04) translateY(-2px)';
        
        this.updateStatus('正在下载...', 'loading');
        
        await this.delay(800);
        
        this.updateStatus('下载成功', 'success');
    }

    async animateCardHover(card) {
        card.style.transform = 'scale(1.04) translateY(-2px)';
        await this.delay(200);
        card.style.transform = 'scale(1)';
    }

    switchToHome() {
        const homePage = document.getElementById('homePage');
        const favoritesPage = document.getElementById('favoritesPage');
        const settingsPage = document.getElementById('settingsPage');
        const sidebarHome = document.getElementById('sidebar-home');
        const sidebarFavorites = document.getElementById('sidebar-favorites');
        const sidebarSettings = document.getElementById('sidebar-settings');
        const saveSettingsBtn = document.getElementById('saveSettingsBtn');
        const resetSettingsBtn = document.getElementById('resetSettingsBtn');
        
        favoritesPage.style.display = 'none';
        settingsPage.style.display = 'none';
        homePage.style.display = 'flex';
        
        sidebarHome.classList.add('active');
        sidebarFavorites.classList.remove('active');
        sidebarSettings.classList.remove('active');
        
        saveSettingsBtn.style.display = 'none';
        resetSettingsBtn.style.display = 'none';
        
        this.animatePageTransition(homePage);
    }

    switchToFavorites() {
        const homePage = document.getElementById('homePage');
        const favoritesPage = document.getElementById('favoritesPage');
        const settingsPage = document.getElementById('settingsPage');
        const sidebarHome = document.getElementById('sidebar-home');
        const sidebarFavorites = document.getElementById('sidebar-favorites');
        const sidebarSettings = document.getElementById('sidebar-settings');
        const saveSettingsBtn = document.getElementById('saveSettingsBtn');
        const resetSettingsBtn = document.getElementById('resetSettingsBtn');
        
        homePage.style.display = 'none';
        settingsPage.style.display = 'none';
        favoritesPage.style.display = 'flex';
        
        sidebarHome.classList.remove('active');
        sidebarFavorites.classList.add('active');
        sidebarSettings.classList.remove('active');
        
        saveSettingsBtn.style.display = 'none';
        resetSettingsBtn.style.display = 'none';
        
        this.loadFavorites();
        this.animatePageTransition(favoritesPage);
    }

    switchToSettings() {
        const homePage = document.getElementById('homePage');
        const favoritesPage = document.getElementById('favoritesPage');
        const settingsPage = document.getElementById('settingsPage');
        const sidebarHome = document.getElementById('sidebar-home');
        const sidebarFavorites = document.getElementById('sidebar-favorites');
        const sidebarSettings = document.getElementById('sidebar-settings');
        const saveSettingsBtn = document.getElementById('saveSettingsBtn');
        const resetSettingsBtn = document.getElementById('resetSettingsBtn');
        
        homePage.style.display = 'none';
        favoritesPage.style.display = 'none';
        settingsPage.style.display = 'flex';
        
        sidebarHome.classList.remove('active');
        sidebarFavorites.classList.remove('active');
        sidebarSettings.classList.add('active');
        
        saveSettingsBtn.style.display = 'block';
        resetSettingsBtn.style.display = 'block';
        
        this.animatePageTransition(settingsPage);
    }

    animatePageTransition(page) {
        page.style.opacity = '0';
        page.style.transform = 'translateX(20px)';
        
        setTimeout(() => {
            page.style.transition = 'all 0.3s ease';
            page.style.opacity = '1';
            page.style.transform = 'translateX(0)';
        }, 50);
    }

    animateElement(selector, animation) {
        return new Promise(resolve => {
            const element = document.querySelector(selector);
            element.style.animation = 'none';
            element.offsetHeight;
            element.style.animation = `${animation} 0.5s ease`;
            setTimeout(resolve, 500);
        });
    }

    updateStatus(text, className) {
        const statusBar = document.getElementById('statusBar');
        statusBar.textContent = text;
        statusBar.className = `status-bar ${className}`;
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const demo = new DemoAnimation();
    
    const style = document.createElement('style');
    style.textContent = `
        @keyframes bounce {
            0%, 100% { transform: translateY(0); }
            50% { transform: translateY(-20px); }
        }
    `;
    document.head.appendChild(style);
});
