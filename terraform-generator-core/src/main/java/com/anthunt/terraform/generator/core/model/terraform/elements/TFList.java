package com.anthunt.terraform.generator.core.model.terraform.elements;

import com.anthunt.terraform.generator.core.model.terraform.AbstractMarshaller;
import lombok.Builder;
import lombok.Singular;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@ToString
public class TFList extends AbstractMarshaller<TFList> {

    @Singular private List<AbstractMarshaller<?>> lists;

    @Builder.Default
    private boolean isLineIndent = true;

    public static TFList build(List<AbstractMarshaller<?>> lists) {
        return TFList.builder().lists(lists).build();
    }

    @Override
    protected String unmarshalling(int tabSize) {
        int nextTabSize = tabSize + 1;

        String indent = "\t".repeat(tabSize + 1);
        if (isLineIndent && lists.size() > 1) {
            return lists.stream().map(o -> o.unmarshall(nextTabSize)).collect(Collectors.joining(",\n" + indent, "[\n" + indent, "\n" + "\t".repeat(tabSize) + "]\n"));
        } else if (!isLineIndent && lists.size() > 1) {
            return lists.stream().map(o -> o.unmarshall(nextTabSize)).collect(Collectors.joining(", ", "[", "]"));
        } else {
            return lists.stream().map(o -> o.unmarshall(nextTabSize)).collect(Collectors.joining(", ", "[", "]\n"));
        }
    }
}
