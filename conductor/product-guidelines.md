# Product Guidelines

## Prose Style
- **Technical & Concise:** エンジニア向けのドキュメントとして、簡潔で直接的な日本語を使用する。
- **Consistent Terminology:** 日本語と英語のドキュメントにおいて、専門用語（「単語区切り」「Caret Movement」など）の対訳に一貫性を持たせる。

## UX Principles
- **Seamless Integration:** プラグインが IDE の一部として自然に溶け込み、ユーザーが追加のツールを意識せずに操作できることを目指す。既存のショートカットキー体系を尊重し、設定の容易さを追求する。
- **High Performance Flow:** カーソル移動の際にレイテンシを発生させないよう、パースアルゴリズムを最適化し、入力の流れを止めない。

## Technical & Architectural Guidelines
- **Strict Layered Architecture:** Domain, Application, Presentation, Settings の各層を明確に分離する。特に、IDE 固有の API への依存は可能な限り外層（Presentation, Settings）に閉じ込め、コアロジックを Pure Kotlin に保つ。
- **Test-driven Reliability:** `WordParser` などの主要なロジックには包括的なユニットテストを実装し、継続的な品質と回帰バグの防止を保証する。
- **Modular & Maintainable:** コードの変更が局所적になるよう責務を適切に分割し、将来の IDE バージョンアップに伴うメンテナンスコストを最小限に抑える。

## Branding & Visual Style
- **Standard IDE Look & Feel:** JetBrains 製 IDE の標準的な UI ガイドラインを遵守し、ユーザーに安心感を与えるデザインを採用する。アイコン等も IDE のトンマナに合わせる。
