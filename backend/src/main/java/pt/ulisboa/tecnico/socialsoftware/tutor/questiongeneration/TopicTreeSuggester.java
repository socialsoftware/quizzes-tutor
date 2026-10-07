package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicSourceDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicTreeDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgSectionDto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Proposes a topic tree for a document from its sections: every heading becomes a topic under
 * the topic of the heading above it, and the sections that have text become the sources of
 * their topic. It stores nothing; the teacher edits the proposal before saving it.
 */
public final class TopicTreeSuggester {
    // The service writes a section path as "Chapter > Section > Subsection"
    private static final String SEPARATOR = " > ";

    private TopicTreeSuggester() {
    }

    /**
     * @param sections         the document's sections in reading order
     * @param existingTopicIds ids of the course's topics by name; a node with a known name joins that topic
     */
    public static TopicTreeDto suggest(String materialId, List<AqgSectionDto> sections, Map<String, Integer> existingTopicIds) {
        Map<String, TopicTreeDto.Node> nodes = new LinkedHashMap<>();

        for (AqgSectionDto section : sections) {
            String[] titles = section.path().split(Pattern.quote(SEPARATOR));
            String parentKey = null;
            StringBuilder path = new StringBuilder();

            for (int depth = 0; depth < titles.length; depth++) {
                if (depth > 0)
                    path.append(SEPARATOR);
                path.append(titles[depth]);

                String key = path.toString();
                String parent = parentKey;
                String title = titles[depth];
                TopicTreeDto.Node node = nodes.computeIfAbsent(key, k -> new TopicTreeDto.Node(k, title.trim(), parent));
                parentKey = key;

                if (depth == titles.length - 1) {
                    node.getSources().add(new TopicSourceDto(materialId, section.path()));
                    node.setChunkCount(section.chunkCount());
                }
            }
        }

        nameNodes(nodes, existingTopicIds);
        return new TopicTreeDto(new ArrayList<>(nodes.values()));
    }

    /** Topic names are unique in a course, so a repeated heading ("Introduction") is told apart by its parent. */
    private static void nameNodes(Map<String, TopicTreeDto.Node> nodes, Map<String, Integer> existingTopicIds) {
        Set<String> used = new HashSet<>();

        for (TopicTreeDto.Node node : nodes.values()) {
            String base = node.getName();
            String name = base;

            if (used.contains(name) && node.getParentKey() != null)
                name = base + " (" + nodes.get(node.getParentKey()).getName() + ")";
            for (int copy = 2; used.contains(name); copy++)
                name = base + " (" + copy + ")";

            node.setName(name);
            used.add(name);
            node.setExistingTopicId(existingTopicIds.get(name));
        }
    }
}
