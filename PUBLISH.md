# Publish to GitHub

## 1. Create the repo

On GitHub: **New repository** -> name it `undercover-dumper` -> public -> no README.

## 2. Push this folder

```bat
cd path\to\undercover-dumper
git add .
git commit -m "Initial release"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/undercover-dumper.git
git push -u origin main
```

Replace `YOUR_USERNAME` with your GitHub account.

## 3. Enable GitHub Pages

Repo -> **Settings** -> **Pages** -> **Build and deployment**:

- Source: **GitHub Actions**

The workflow in `.github/workflows/pages.yml` deploys `docs/` on every push to `main`.

Site URL: `https://YOUR_USERNAME.github.io/undercover-dumper/`

## 4. Create a release (for jar download button)

Repo -> **Releases** -> **Draft a new release**:

- Tag: `v1.0.0`
- Attach `ClassDumpAgent.jar` from this folder
- Publish

The site download link points to `/releases/latest/download/ClassDumpAgent.jar`.

## 5. Optional local preview

Open `docs/index.html` in a browser. Download links work after the repo is live.