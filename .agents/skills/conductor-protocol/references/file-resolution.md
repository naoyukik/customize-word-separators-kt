# **Universal File Resolution Protocol**

プロジェクト内（Project Root）または特定のトラック内（Track Context）でファイルを特定するための絶対的な手順を規定する。

## **1. インデックスの特定**

解決対象のコンテキストに応じたインデックスファイルを特定せよ。

- **Project Context**: `conductor/index.md`
- **Track Context**:
    1.  **Tracks Registry**（`conductor/tracks.md`）を読み込む。
    2.  対象の `<track_id>` に対応するエントリーを見つける。
    3.  レジストリ内のリンクに従い、トラックのディレクトリを特定する。インデックスは `<track_folder>/index.md` である。
    4.  **Fallback**: レジストリが未更新または壊れている場合、デフォルトパス `<Tracks Directory>/<track_id>/index.md` を使用せよ。

## **2. インデックスの確認**

特定した `index.md` を読み込み、目的のラベル（例：「Product Definition」）に一致、または意味的に近いリンクを探せ。

## **3. パスの解決**

リンクが見つかった場合、`index.md` が存在するディレクトリからの **相対パス** として解決せよ。
- *例*: `conductor/index.md` が `./workflow.md` にリンクしている場合、絶対パスは `conductor/workflow.md` となる。

## **4. フォールバック (Default Paths)**

インデックスが存在しない、またはリンクが見つからない場合は、以下の標準デフォルトパスを使用せよ。

### **Standard Default Paths (Project)**
- **Product Definition**: `conductor/product.md`
- **Tech Stack**: `conductor/tech-stack.md`
- **Workflow**: `conductor/workflow.md`
- **Product Guidelines**: `conductor/product-guidelines.md`
- **Tracks Registry**: `conductor/tracks.md`
- **Tracks Directory**: `conductor/tracks/`

### **Standard Default Paths (Track)**
- **Specification**: `conductor/tracks/<track_id>/spec.md`
- **Implementation Plan**: `conductor/tracks/<track_id>/plan.md`
- **Metadata**: `conductor/tracks/<track_id>/metadata.json`

## **5. 存在確認の義務**

解決したパスがディスク上に実際に存在することを、必ずツール（`list_directory` 等）で確認せよ。
