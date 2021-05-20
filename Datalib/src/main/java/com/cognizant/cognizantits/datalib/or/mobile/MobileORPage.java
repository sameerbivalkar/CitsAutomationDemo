/*
 * Copyright 2014 - 2017 Cognizant Technology Solutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognizant.cognizantits.datalib.or.mobile;

import com.cognizant.cognizantits.datalib.component.utils.FileUtils;
import com.cognizant.cognizantits.datalib.or.common.ORPageInf;
import com.cognizant.cognizantits.datalib.or.common.ORUtils;
import com.cognizant.cognizantits.datalib.or.common.ObjectGroup;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MobileORPage implements ORPageInf<MobileORObject, MobileOR> {

    @JacksonXmlProperty(isAttribute = true, localName = "ref")
    private String name;

    @JacksonXmlProperty(isAttribute = true)
    private String packageName;

    @JacksonXmlProperty(localName = "ObjectGroup")
    @JacksonXmlElementWrapper(useWrapping = false, localName = "ObjectGroup")
    private List<ObjectGroup<MobileORObject>> objectGroups;

    @JsonIgnore
    private MobileOR root;

    public MobileORPage() {
        this.objectGroups = new ArrayList<>();
    }

    public MobileORPage(String name, MobileOR root) {
        this.name = name;
        this.root = root;
        this.packageName = "";
        this.objectGroups = new ArrayList<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public List<ObjectGroup<MobileORObject>> getObjectGroups() {
        return objectGroups;
    }

    @Override
    public void setObjectGroups(List<ObjectGroup<MobileORObject>> objectGroups) {
        this.objectGroups = objectGroups;
        for (ObjectGroup<MobileORObject> objectGroup : objectGroups) {
            objectGroup.setParent(this);
        }
    }

    @JsonIgnore
    @Override
    public void removeFromParent() {
        root.setSaved(false);
        root.getPages().remove(this);
        FileUtils.deleteFile(getRepLocation());
    }

    @JsonIgnore
    @Override
    public ObjectGroup<MobileORObject> getObjectGroupByName(String groupName) {
        for (ObjectGroup<MobileORObject> group : objectGroups) {
            if (group.getName().equalsIgnoreCase(groupName)) {
                return group;
            }
        }
        return null;
    }

    @JsonIgnore
    @Override
    public ObjectGroup<MobileORObject> addObjectGroup() {
        String oName = "MObjectGroup";
        int i = 0;
        String objectName;
        do {
            objectName = oName + i++;
        } while (getObjectGroupByName(objectName) != null);

        return addObjectGroup(objectName);
    }

    @JsonIgnore
    @Override
    public ObjectGroup<MobileORObject> addObjectGroup(String groupName) {
        if (getObjectGroupByName(groupName) == null) {
            ObjectGroup<MobileORObject> group = new ObjectGroup<>(groupName, this);
            objectGroups.add(group);
            new File(group.getRepLocation()).mkdirs();
            group.addObject(groupName);
            root.setSaved(false);
            return group;
        }
        return null;
    }

    @JsonIgnore
    @Override
    public MobileORObject getNewObject(String objectName, ObjectGroup<MobileORObject> group) {
        return new MobileORObject(objectName, group);
    }

    @JsonIgnore
    @Override
    public MobileORObject addObject() {
        String oName = "MObject";
        int i = 0;
        String objectName;
        do {
            objectName = oName + i++;
        } while (getObjectGroupByName(objectName) != null);

        return addObject(objectName);
    }

    @JsonIgnore
    @Override
    public MobileORObject addObject(String objectName) {
        ObjectGroup<MobileORObject> group = addObjectGroup(objectName);
        if (group != null) {
            return group.getObjects().get(0);
        }
        return null;
    }

    @JsonIgnore
    @Override
    public void deleteObjectGroup(String groupName) {
        ObjectGroup<MobileORObject> group = getObjectGroupByName(groupName);
        if (group != null) {
            objectGroups.remove(group);
            root.setSaved(false);
        }
    }

    @JsonIgnore
    @Override
    public TreeNode getChildAt(int i) {
        if (objectGroups.get(i).getChildCount() > 1) {
            return objectGroups.get(i);
        }
        return objectGroups.get(i).getChildAt(0);
    }

    @JsonIgnore
    @Override
    public int getChildCount() {
        return objectGroups == null ? 0
                : objectGroups.size();
    }

    @JsonIgnore
    @Override
    public MobileOR getParent() {
        return root;
    }

    @JsonIgnore
    @Override
    public int getIndex(TreeNode tn) {
        return objectGroups.indexOf(tn);
    }

    @JsonIgnore
    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isLeaf() {
        return getChildCount() == 0;
    }

    @JsonIgnore
    @Override
    public Enumeration children() {
        return Collections.enumeration(objectGroups);
    }

    @JsonIgnore
    @Override
    public MobileOR getRoot() {
        return root;
    }

    @JsonIgnore
    @Override
    public void setRoot(MobileOR root) {
        this.root = root;
    }

    @Override
    public String toString() {
        return name;
    }

    @JsonIgnore
    @Override
    public TreeNode[] getPath() {
        return (TreeNode[]) ORUtils.getPath(this).getPath();
    }

    @JsonIgnore
    @Override
    public TreePath getTreePath() {
        return ORUtils.getPath(this);
    }

    @Override
    public Boolean rename(String newName) {
        if (getParent().getPageByName(newName) == null) {
            if (FileUtils.renameFile(getRepLocation(), newName)) {
                getRoot().getObjectRepository().renamePage(this, newName);
                setName(newName);
                getParent().setSaved(false);
                return true;
            }
        }
        return false;
    }

    @JsonIgnore
    @Override
    public String getRepLocation() {
        return getParent().getRepLocation() + File.separator + getName();
    }

    @JsonIgnore
    @Override
    public void sort() {
        ORUtils.sort(this);
    }
}
