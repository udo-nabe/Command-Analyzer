# Written By ChatGPT

import requests
import csv
from datetime import datetime
import os

# リポジトリ情報
OWNER = "udo-nabe"
REPO = "Command-Analyzer"

# CSV保存先
CSV_PATH = "GitHubDownloads.csv"

# GitHub API 取得
url = f"https://api.github.com/repos/{OWNER}/{REPO}/releases"
headers = {"User-Agent": "Python"}
response = requests.get(url, headers=headers).json()

# 現在時刻
now = datetime.utcnow().strftime("%Y-%m-%d %H:%M:%S")

# 最新バージョン(人間が追加)
LATEST_VERSION = "1.0.2"

# CSVに書き込むデータ
rows = []
for release in response:
    release_name = release["name"]
    for asset in release["assets"]:
        if LATEST_VERSION in asset["name"] 
        rows.append([now, release_name, asset["name"], asset["download_count"]])

# CSVが存在しない場合はヘッダ付きで作成
file_exists = os.path.isfile(CSV_PATH)
with open(CSV_PATH, "a", newline="", encoding="utf-8") as f:
    writer = csv.writer(f)
    if not file_exists:
        writer.writerow(["Date", "Release", "Asset", "Downloads"])
    writer.writerows(rows)
