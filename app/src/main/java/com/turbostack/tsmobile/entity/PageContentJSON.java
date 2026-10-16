package com.example.app.entity;

import java.util.ArrayList;
import java.util.List;

public class PageContentJSON {
    public String id;
    public String tag;
    public String content;
    public String className;
    public String href;
    public String styles;
    public List<PageContentJSON> children;
    public String inputType;
    public String placeholder;
    public String form_type;
    public String value;
    public String action;
    public String name;
    public boolean checked;
    public boolean passwordViewToggler;
    public String targetedPassword;
    public String htmlId;

    public PageContentJSON() {
        this.children = new ArrayList<>();
    }

    public PageContentJSON(String id, String tag, String content, String className, String href,
                           String styles, List<PageContentJSON> children, String inputType,
                           String placeholder, String formType, String value, String formAction,
                           String name, boolean checked, boolean passwordViewToggler,
                           String targetedPassword, String htmlId) {
        this.id = id;
        this.tag = tag;
        this.content = content;
        this.className = className;
        this.href = href;
        this.styles = styles;
        this.children = children;
        this.inputType = inputType;
        this.placeholder = placeholder;
        this.form_type = formType;
        this.value = value;
        this.action = formAction;
        this.name = name;
        this.checked = checked;
        this.passwordViewToggler = passwordViewToggler;
        this.targetedPassword = targetedPassword;
        this.htmlId = htmlId;
    }

    public PageContent toModel() {
        List<PageContent> childModels = new ArrayList<>();
        if (this.children != null) {
            for (PageContentJSON child : this.children) {
                childModels.add(child.toModel());
            }
        }
        PageContent pc = new PageContent();
        pc.setId(this.id);
        pc.setTag(this.tag);
        pc.setContent(this.content);
        pc.setClassName(this.className);
        pc.setHref(this.href);
        pc.setStyles(this.styles);
        pc.setChildren(childModels);
        pc.setInputType(this.inputType);
        pc.setPlaceholder(this.placeholder);
        pc.setFormType(this.form_type);
        pc.setValue(this.value);
        pc.setFormAction(this.action);
        pc.setName(this.name);
        pc.setChecked(this.checked);
        pc.setPasswordViewToggler(this.passwordViewToggler);
        pc.setTargetedPassword(this.targetedPassword);
        pc.setHtmlId(this.htmlId);
        return pc;
    }
}