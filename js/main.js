import {
    getJSON,
    setInner,
    setText,
    setHref,
    show,
    hide,
    formatBytes,
    formatDate
} from '../croot.js';

// Project GitHub configuration
const GITHUB_OWNER = 'zahranur4';
const GITHUB_REPO = 'mapdroid';
const API_LATEST_RELEASE = `https://api.github.com/repos/${GITHUB_OWNER}/${GITHUB_REPO}/releases/latest`;
const DIRECT_DOWNLOAD_FALLBACK = `https://github.com/${GITHUB_OWNER}/${GITHUB_REPO}/releases/latest/download/mapdroid.apk`;
const RELEASES_PAGE = `https://github.com/${GITHUB_OWNER}/${GITHUB_REPO}/releases`;

/**
 * Initialize application logic once DOM is loaded
 */
document.addEventListener('DOMContentLoaded', () => {
    // Setup initial fallback links
    setupDefaultLinks();

    // Fetch latest release info via croot getJSON helper
    fetchLatestRelease();
});

function setupDefaultLinks() {
    setHref('btnDownloadHero', DIRECT_DOWNLOAD_FALLBACK);
    setHref('btnDownloadNav', DIRECT_DOWNLOAD_FALLBACK);
    setHref('btnDownloadCard', DIRECT_DOWNLOAD_FALLBACK);
    setHref('linkViewReleases', RELEASES_PAGE);
}

function fetchLatestRelease() {
    setInner('releaseStatusBadge', '<span class="animate-pulse">🔄 Memeriksa rilis terbaru...</span>');

    getJSON(API_LATEST_RELEASE, (data, error) => {
        if (error || !data) {
            console.log('[Mapdroid] Rilis belum tersedia atau batasan API GitHub:', error);
            handleNoReleaseFound();
            return;
        }

        renderReleaseInfo(data);
    });
}

function renderReleaseInfo(release) {
    const tagName = release.tag_name || 'v1.0.0';
    const releaseName = release.name || `Release ${tagName}`;
    const publishedAt = formatDate(release.published_at);
    const releaseUrl = release.html_url || RELEASES_PAGE;

    // Look for apk asset
    let apkAsset = null;
    if (Array.isArray(release.assets) && release.assets.length > 0) {
        apkAsset = release.assets.find(a => a.name.endsWith('.apk')) || release.assets[0];
    }

    const downloadUrl = apkAsset ? apkAsset.browser_download_url : DIRECT_DOWNLOAD_FALLBACK;
    const fileSize = apkAsset ? formatBytes(apkAsset.size) : 'Siap Diunduh';
    const downloadCount = apkAsset ? (apkAsset.download_count || 0) : 0;

    // Update download buttons
    setHref('btnDownloadHero', downloadUrl);
    setHref('btnDownloadNav', downloadUrl);
    setHref('btnDownloadCard', downloadUrl);
    setHref('linkViewReleases', releaseUrl);

    // Update badges and labels
    setText('heroVersionTag', tagName);
    setText('heroFileSize', fileSize);
    setText('cardVersionTag', tagName);
    setText('cardReleaseName', releaseName);
    setText('cardPublishedDate', publishedAt);
    setText('cardFileSize', fileSize);
    setText('cardDownloadCount', `${downloadCount} kali diunduh`);
    setText('cardAssetName', apkAsset ? apkAsset.name : 'mapdroid.apk');

    setInner('releaseStatusBadge', `
        <span class="inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold bg-emerald-100 text-emerald-800 border border-emerald-300">
            <span class="w-2 h-2 mr-1.5 rounded-full bg-emerald-500 animate-ping"></span>
            Tersedia: ${tagName} (Aktif)
        </span>
    `);

    // Render release notes / description if available
    if (release.body) {
        const simpleFormattedBody = release.body
            .replace(/### (.*?)\n/g, '<h4 class="font-bold text-slate-800 mt-2 mb-1">$1</h4>')
            .replace(/## (.*?)\n/g, '<h3 class="font-bold text-slate-900 mt-3 mb-1">$1</h3>')
            .replace(/- \*\*(.*?)\*\*/g, '• <strong>$1</strong>')
            .replace(/\n/g, '<br/>');

        setInner('cardReleaseNotes', simpleFormattedBody);
        show('releaseNotesSection');
    }
}

function handleNoReleaseFound() {
    setInner('releaseStatusBadge', `
        <span class="inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold bg-amber-100 text-amber-800 border border-amber-300">
            <span class="w-2 h-2 mr-1.5 rounded-full bg-amber-500"></span>
            Menunggu Rilis Otomatis Pertama
        </span>
    `);

    setText('heroVersionTag', 'v1.0.0');
    setText('heroFileSize', 'Release Build');
    setText('cardVersionTag', 'v1.0.x (Auto)');
    setText('cardReleaseName', 'Mapdroid Release Terjadwal');
    setText('cardPublishedDate', 'Akan rilis otomatis saat push ke branch main');
    setText('cardFileSize', 'Signed APK');
    setText('cardDownloadCount', 'GitHub Actions CI/CD');
    setText('cardAssetName', 'mapdroid.apk');

    setInner('cardReleaseNotes', `
        <p class="text-sm text-slate-600">
            Workflow GitHub Actions telah dikonfigurasi. Begitu Anda melakukan <code>git push origin main</code>,
            GitHub Actions akan otomatis membangun file APK, memberi tag versi, dan merilisnya ke halaman GitHub Releases.
            Link download langsung di bawah akan aktif secara otomatis.
        </p>
    `);
    show('releaseNotesSection');
}
