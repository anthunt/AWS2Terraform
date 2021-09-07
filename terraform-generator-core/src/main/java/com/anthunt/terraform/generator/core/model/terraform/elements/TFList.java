package com.anthunt.terraform.generator.core.model.terraform.elements;

import com.anthunt.terraform.generator.core.model.terraform.AbstractMarshaller;
import lombok.Builder;
import lombok.Singular;
import lombok.ToString;

import java.util.List;

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
        StringBuffer stringBuffer = new StringBuffer();
        int nextTabSize = tabSize + 1;

        stringBuffer.append("[").append(lists.size() > 1 && isLineIndent ? "\n" : "");

        for (int inx=0; inx < lists.size(); inx++) {
            AbstractMarshaller<?> o = lists.get(inx);
            stringBuffer
                    .append( lists.size() > 1 && isLineIndent ? "\t".repeat(tabSize + 1) : "")
                    .append( o.unmarshall(nextTabSize) )
                    .append( lists.size() > 1 && inx < lists.size() - 1 ? ", " : "" )
                    .append( lists.size() > 1 && isLineIndent ? "\n" : "");
        }

//        lists.forEach(o ->{
//            stringBuffer
//                    .append( lists.size() > 1 && isLineIndent ? "\t".repeat(tabSize + 1) : "")
//                    .append( o.unmarshall(nextTabSize) )
//                    .append( lists.size() > 1 ? ", " : "" )
//                    .append( lists.size() > 1 && isLineIndent ? "\n" : "");
//        });
        stringBuffer
                .append( lists.size() > 1 && isLineIndent ? "\t".repeat(tabSize) : "")
                .append("]")
                .append( isLineIndent ? "\n" : "" );

        return stringBuffer.toString();
    }
}
