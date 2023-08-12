# Customize Word Separators

![Build](https://github.com/naoyukik/custom-word-separators-kt/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/13613-customize-word-separators.svg)](https://plugins.jetbrains.com/plugin/13613-customize-word-separators)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/13613-customize-word-separators.svg)](https://plugins.jetbrains.com/plugin/13613-customize-word-separators)

<!-- Plugin description -->
This is a word separator custom plugin for the IntelliJ platform.  

By changing the position of the word separator, the cursor can be moved by each type of kanji, hiragana, katakana, and alphanumeric character when moving the cursor.

IntelliJプラットフォーム向けの単語区切りカスタムプラグインです。  

単語区切り位置を変更することで、カーソル移動時に漢字・ひらがな・カタカナ・英数字を判別し、種別毎にカーソルが移動可能となります。
<!-- Plugin description end -->

## Installation

- Using IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "Customize Word Separators"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/naoyukik/custom-word-separators-kt/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

## Getting started
After installation, set the shortcut key.

1. search for `move caret to` in File | Settings | Keymap
2. find `Customize word separators` in the found list
3. Set shortcut keys for the four commands under `Customize word separators

If you want to replace the current shortcut key for moving the cursor, press the Delete button when asked if you want to delete the other keys.

インストール後、ショートカットキーを設定します。

1. File | Settings | Keymap から `move caret to` を検索
2. 見つかったリストから `Customize word separators` を探す
3. `Customize word separators`の配下にある4つのコマンドにショートカットを設定

現在のカーソル移動のショートカットキーを差し替える場合、設定時に「他のキーを削除するか」と聞かれるので削除ボタンを押します。

## Special Thanks
- The plugin icon is by my best friend Kaorin. Thank you!

---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
