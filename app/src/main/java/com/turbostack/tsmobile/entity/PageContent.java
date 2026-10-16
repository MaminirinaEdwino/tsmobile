package com.example.app.entity;

import java.util.ArrayList;
import java.util.List;

public class PageContent {
    private String id;
    private String tag;
    private String content;
    private String className;
    private String href;
    private String styles;
    private List<PageContent> children;
    private String inputType;
    private String placeholder;
    private String formType;
    private String formAction;
    private String value;
    private String name;
    private boolean checked;
    private boolean passwordViewToggler;
    private String targetedPassword;
    private String htmlId;

    public PageContent() {
        this.children = new ArrayList<>();
    }

    public PageContentJSON toJSON() {
        List<PageContentJSON> childJSONs = new ArrayList<>();
        if (this.children != null) {
            for (PageContent child : this.children) {
                childJSONs.add(child.toJSON());
            }
        }
        return new PageContentJSON(
            this.id, this.tag, this.content, this.className, this.href,
            this.styles, childJSONs, this.inputType, this.placeholder,
            this.formType, this.value, this.formAction, this.name,
            this.checked, this.passwordViewToggler, this.targetedPassword, this.htmlId
        );
    }

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getHref() { return href; }
    public void setHref(String href) { this.href = href; }

    public String getStyles() { return styles; }
    public void setStyles(String styles) { this.styles = styles; }

    public List<PageContent> getChildren() { return children; }
    public void setChildren(List<PageContent> children) { this.children = children; }

    public String getInputType() { return inputType; }
    public void setInputType(String inputType) { this.inputType = inputType; }

    public String getPlaceholder() { return placeholder; }
    public void setPlaceholder(String placeholder) { this.placeholder = placeholder; }

    public String getFormType() { return formType; }
    public void setFormType(String formType) { this.formType = formType; }

    public String getFormAction() { return formAction; }
    public void setFormAction(String formAction) { this.formAction = formAction; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isChecked() { return checked; }
    public void setChecked(boolean checked) { this.checked = checked; }

    public boolean isPasswordViewToggler() { return passwordViewToggler; }
    public void setPasswordViewToggler(boolean passwordViewToggler) { this.passwordViewToggler = passwordViewToggler; }

    public String getTargetedPassword() { return targetedPassword; }
    public void setTargetedPassword(String targetedPassword) { this.targetedPassword = targetedPassword; }

    public String getHtmlId() { return htmlId; }
    public void setHtmlId(String htmlId) { this.htmlId = htmlId; }
} 
