---
layout: page
title: Documentation guide
---

**Setting up and maintaining the project website:**

* We use [**Jekyll**](https://jekyllrb.com/) to manage documentation.
* The `docs/` folder is used for documentation.
* To learn how to set up and maintain the project website, follow the guide [_[se-edu/guides] **Using Jekyll for project documentation**_](https://se-education.org/guides/tutorials/jekyll.html).
* Note these points when adapting the documentation to a different project/product:
  * The 'Site-wide settings' section of the page linked above has information on how to update site-wide elements such as the top navigation bar.
  * :bulb: In addition to updating content files, check `docs/_config.yml` when navigation, the site title, or page metadata changes. Inspect the rendered site after changing its theme or styles.
* If you are using IntelliJ for editing documentation files, you can consider enabling 'soft wrapping' for `*.md` files, as explained in [_[se-edu/guides] **Intellij IDEA: Useful settings**_](https://se-education.org/guides/tutorials/intellijUsefulSettings.html#enabling-soft-wrapping)


**Style guidance:**

* Follow the [**_Google developer documentation style guide_**](https://developers.google.com/style).

* Also relevant is the [_[se-edu/guides] **Markdown coding standard**_](https://se-education.org/guides/conventions/markdown.html)

* Follow the repository's [documentation quality gate](https://github.com/AY2627S1-CS2103T-W13-2/tp/blob/master/AGENTS.md#documentation-and-uml): verify examples against the actual command parser, separate implemented behavior from future requirements, and describe limitations and recovery steps honestly.

* Before review, check local links and image paths, inspect the rendered UG and DG, and compare each modified diagram's `.puml` source and PNG with the implementation. For a release, rerun user-facing examples against the exact packaged JAR and record its checksum.

**Diagrams:**

* See the [_[se-edu/guides] **Using PlantUML**_](https://se-education.org/guides/tutorials/plantUml.html)

**Converting a document to the PDF format:**

* See the guide [_[se-edu/guides] **Saving web documents as PDF files**_](https://se-education.org/guides/tutorials/savingPdf.html)
