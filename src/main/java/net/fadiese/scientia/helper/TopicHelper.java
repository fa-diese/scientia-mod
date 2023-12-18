package net.fadiese.scientia.helper;

import net.fadiese.scientia.client.tooltip.CompatibleTopicsTooltip;
import net.fadiese.scientia.data.ResearchTopic;
import net.fadiese.scientia.recipe.CollationTableRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class TopicHelper {

    private static TopicHelper instance;

    private HashMap<String, Component> topicComponents;
    private HashMap<String, CompatibleTopicsTooltip> compatibleTopicsTooltips;

    private TopicHelper(HashMap<String, Component> topicComponents, HashMap<String, CompatibleTopicsTooltip> compatibleTopicsTooltips) {
        this.topicComponents = topicComponents;
        this.compatibleTopicsTooltips = compatibleTopicsTooltips;
    }

    public static void initializeInstance(List<ResearchTopic> topics, List<CollationTableRecipe> collationRecipes) {
        if (instance != null) {
            instance.updateInstance(topics, collationRecipes);
        } else {
            HashMap<String, Component> topicComponents = new HashMap<>();
            HashMap<String, CompatibleTopicsTooltip> compatibleTopicsTooltips = new HashMap<>();
            topics.forEach(elt -> topicComponents.put(elt.topic(), Component.literal(elt.topic()).withStyle(Style.EMPTY.withColor(elt.color()))));
            collationRecipes.forEach(elt -> {
                if (topicComponents.containsKey(elt.firstTopic()) && topicComponents.containsKey(elt.secondTopic())) {
                    addNewCompatibleTopicComponent(elt.firstTopic(), topicComponents.get(elt.secondTopic()), compatibleTopicsTooltips);
                    if (!elt.firstTopic().equals(elt.secondTopic())) {
                        addNewCompatibleTopicComponent(elt.secondTopic(), topicComponents.get(elt.firstTopic()), compatibleTopicsTooltips);
                    }
                }
            });
            instance = new TopicHelper(topicComponents, compatibleTopicsTooltips);
        }
    }

    public static TopicHelper getInstance() {
        return instance;
    }

    private static void addNewCompatibleTopicComponent(String topic, Component compatibleTopicComponent, HashMap<String, CompatibleTopicsTooltip> compatibleTopicsTooltips) {
        if (compatibleTopicsTooltips.get(topic) != null) {
            compatibleTopicsTooltips.get(topic).compatibleTopics().add(compatibleTopicComponent);
        } else {
            List<Component> compatTopicComponentList = new ArrayList<>();
            compatTopicComponentList.add(compatibleTopicComponent);
            compatibleTopicsTooltips.put(topic, new CompatibleTopicsTooltip(compatTopicComponentList));
        }
    }

    private void updateInstance(List<ResearchTopic> topics, List<CollationTableRecipe> collationRecipes) {
        topicComponents = new HashMap<>();
        topics.forEach(elt -> {
            topicComponents.put(elt.topic(), Component.literal(elt.topic()).withStyle(Style.EMPTY.withColor(elt.color())));
        });
        compatibleTopicsTooltips = new HashMap<>();
        collationRecipes.forEach(elt -> {
            if (topicComponents.containsKey(elt.firstTopic()) && topicComponents.containsKey(elt.secondTopic())) {
                updateTopicCompatibility(elt.firstTopic(), elt.secondTopic());
                if (!elt.firstTopic().equals(elt.secondTopic())) {
                    updateTopicCompatibility(elt.secondTopic(), elt.firstTopic());
                }
            }
        });
    }

    private void updateTopicCompatibility(String topic, String compatibleTopic) {
        if (compatibleTopicsTooltips.get(topic) != null) {
            compatibleTopicsTooltips.get(topic).compatibleTopics().add(topicComponents.get(compatibleTopic));
        } else {
            List<Component> compatTopicComponentList = new ArrayList<>();
            compatTopicComponentList.add(topicComponents.get(compatibleTopic));
            compatibleTopicsTooltips.put(topic, new CompatibleTopicsTooltip(compatTopicComponentList));
        }
    }

    public Component getTopicComponent(String topic) {
        return topicComponents.get(topic);
    }

    public boolean isTopicRegistered(String topic) {
        return compatibleTopicsTooltips.containsKey(topic);
    }

    public Optional<TooltipComponent> getCompatibleTopicsTooltip(String topic) {
        return Optional.of(compatibleTopicsTooltips.get(topic));
    }
}
