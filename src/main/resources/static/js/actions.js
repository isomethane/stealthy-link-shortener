function copyShortLink() {
    let shortLink = document.getElementById("short-link").textContent;
    let copyButton = document.getElementById("copy-button");

    navigator.clipboard.writeText(shortLink).then(() => {
        copyButton.classList.add("copy-done");
        setTimeout(() => copyButton.classList.remove("copy-done"), 1000);
    });
}
