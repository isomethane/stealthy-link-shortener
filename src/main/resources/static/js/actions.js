function copyShortLink() {
    let shortLink = document.getElementById("short-link").textContent;
    let copyButton = document.getElementById("copy-button");

    navigator.clipboard.writeText(shortLink).then(() => {
        copyButton.classList.add("copy-done");
        setTimeout(() => copyButton.classList.remove("copy-done"), 1000);
    });
}

function onSafeOptionChange(radio) {
    let customUrlInput = document.getElementById("custom-url");
    if (radio.id === "Custom" && radio.checked) {
        customUrlInput.removeAttribute("disabled");
        customUrlInput.focus();
    } else {
        customUrlInput.setAttribute("disabled", "");
    }
}
