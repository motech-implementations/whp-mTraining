package org.motechproject.whp.mtraining.domain;

import org.motechproject.whp.mtraining.dto.ContentDto;

import java.util.ArrayList;
import java.util.List;

/**
 * A tree structured object representing a content and its descendants<br />
 * E.g. A Node representing a chapter will have:<br />
 * {@link Node#nodeType} as {@link NodeType#CHAPTER}<br />
 * {@link Node#nodeData} as an instance of {@link org.motechproject.mtraining.dto.ChapterDto}<br />
 * {@link Node#childNodes} as list of nodes representing messages<br />
 * {@link Node#persistentEntity} as null by default and gets updated to the associated {@link org.motechproject.mtraining.domain.Chapter} domain object after saving
 */

public class Node {
    private NodeType nodeType;
    private ContentDto nodeData;
    private List<Node> childNodes;

    private Content persistentEntity;

    public Node(NodeType nodeType, ContentDto nodeData) {
        this.nodeType = nodeType;
        this.nodeData = nodeData;
        this.childNodes = new ArrayList<>();
    }

    public Node(NodeType nodeType, ContentDto nodeData, List<Node> childNodes) {
        this(nodeType, nodeData);
        this.childNodes = childNodes;
    }

    public List<Node> getChildNodes() {
        return childNodes;
    }

    public ContentDto getNodeData() {
        return nodeData;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public Content getPersistentEntity() {
        return persistentEntity;
    }

    public void setPersistentEntity(Content persistentEntity) {
        this.persistentEntity = persistentEntity;
    }
}
