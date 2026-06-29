# Publish to GitHub

Repo: https://github.com/Undercover-fabric/undercover-dumper  
Pages: https://undercover-fabric.github.io/undercover-dumper/

## 1. Create the repo (if not done)

GitHub -> **New repository** -> name `undercover-dumper` -> public -> no README.

## 2. Push

```bat
cd path\to\undercover-dumper
git remote add origin https://github.com/Undercover-fabric/undercover-dumper.git
git push -u origin main
```

If `origin` already exists:

```bat
git remote set-url origin https://github.com/Undercover-fabric/undercover-dumper.git
git push -u origin main
```

## 3. Enable Pages

Repo -> **Settings** -> **Pages** -> **Build and deployment** -> Source: **GitHub Actions**

## 4. Release (jar download button)

**Releases** -> **Draft a new release**:

- Tag: `v1.0.0`
- Upload `ClassDumpAgent.jar`
- Publish

Download URL: https://github.com/Undercover-fabric/undercover-dumper/releases/latest/download/ClassDumpAgent.jar