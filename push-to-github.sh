#!/bin/bash

# Script untuk push ke GitHub
REPO_URL="https://github.com/RID-07/geli-assessment-test.git"

echo "🔍 Checking remote repository..."
git remote -v

echo ""
echo "📦 Adding remote repository..."
git remote add origin $REPO_URL 2>/dev/null || git remote set-url origin $REPO_URL

echo ""
echo "📝 Staging all files..."
git add .

echo ""
echo "💾 Committing changes..."
git commit -m "Initial commit: add discovery-service, item-service, and order-service"

echo ""
echo "🚀 Pushing to GitHub..."
git branch -M main
git push -u origin main

echo ""
echo "✅ Done! Check your repository at: $REPO_URL"

