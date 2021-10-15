package com.anthunt.terraform.generator.core.model.terraform.elements;

import com.anthunt.terraform.generator.core.model.terraform.AbstractMarshaller;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@ToString
public class TFObject extends AbstractMarshaller<TFObject> {

    private Map<String, AbstractMarshaller<?>> members;

    TFObject(Map<String, AbstractMarshaller<?>> members) {
        this.members = members;
    }

    public static TFObjectBuilder builder() {
        return new TFObjectBuilder();
    }

    @Override
    protected String unmarshalling(int tabSize) {
        StringBuffer stringBuffer = new StringBuffer();
        int nextTabSize = tabSize + 1;
        stringBuffer.append("{\n");
        members.forEach((key, value) -> {
            stringBuffer
                    .append("\t".repeat(tabSize + 1))
                    .append(key)
                    .append(" = ")
                    .append(value.unmarshall(nextTabSize));
        });
        stringBuffer
                .append("\t".repeat(tabSize))
                .append("}");
        return stringBuffer.toString();
    }

    public static class TFObjectBuilder {
        private ArrayList<String> members$key;
        private ArrayList<AbstractMarshaller<?>> members$value;

        TFObjectBuilder() {
        }

        public TFObjectBuilder member(String memberKey, AbstractMarshaller<?> memberValue) {
            if (this.members$key == null) {
                this.members$key = new ArrayList<String>();
                this.members$value = new ArrayList<AbstractMarshaller<?>>();
            }
            this.members$key.add(memberKey);
            this.members$value.add(memberValue);
            return this;
        }

        public TFObjectBuilder memberIf(boolean condition, String memberKey, AbstractMarshaller<?> memberValue) {
            return memberIf(() -> condition, memberKey, memberValue);
        }

        public TFObjectBuilder memberIf(boolean condition, String memberKey, Supplier<AbstractMarshaller<?>> memberValueSupplier) {
            return this.memberIf(() -> condition, memberKey, memberValueSupplier);
        }

        public TFObjectBuilder memberIf(BooleanSupplier booleanSupplier, String memberKey, Supplier<AbstractMarshaller<?>> memberValueSupplier) {
            if (booleanSupplier.getAsBoolean()) {
                return this.member(memberKey, memberValueSupplier.get());
            } else {
                return this;
            }
        }

        public TFObjectBuilder memberIf(BooleanSupplier booleanSupplier, String memberKey, AbstractMarshaller<?> memberValue) {
            if (booleanSupplier.getAsBoolean()) {
                return this.member(memberKey, memberValue);
            } else {
                return this;
            }
        }


        public TFObjectBuilder members(Map<? extends String, ? extends AbstractMarshaller<?>> members) {
            if (this.members$key == null) {
                this.members$key = new ArrayList<String>();
                this.members$value = new ArrayList<AbstractMarshaller<?>>();
            }
            for (final Map.Entry<? extends String, ? extends AbstractMarshaller<?>> $lombokEntry : members.entrySet()) {
                this.members$key.add($lombokEntry.getKey());
                this.members$value.add($lombokEntry.getValue());
            }
            return this;
        }

        public TFObjectBuilder clearMembers() {
            if (this.members$key != null) {
                this.members$key.clear();
                this.members$value.clear();
            }
            return this;
        }

        public TFObject build() {
            Map<String, AbstractMarshaller<?>> members;
            switch (this.members$key == null ? 0 : this.members$key.size()) {
                case 0:
                    members = java.util.Collections.emptyMap();
                    break;
                case 1:
                    members = java.util.Collections.singletonMap(this.members$key.get(0), this.members$value.get(0));
                    break;
                default:
                    members = new java.util.LinkedHashMap<String, AbstractMarshaller<?>>(this.members$key.size() < 1073741824 ? 1 + this.members$key.size() + (this.members$key.size() - 3) / 3 : Integer.MAX_VALUE);
                    for (int $i = 0; $i < this.members$key.size(); $i++)
                        members.put(this.members$key.get($i), (AbstractMarshaller<?>) this.members$value.get($i));
                    members = java.util.Collections.unmodifiableMap(members);
            }

            return new TFObject(members);
        }

        public String toString() {
            return "TFObject.TFObjectBuilder(members$key=" + this.members$key + ", members$value=" + this.members$value + ")";
        }
    }
}
