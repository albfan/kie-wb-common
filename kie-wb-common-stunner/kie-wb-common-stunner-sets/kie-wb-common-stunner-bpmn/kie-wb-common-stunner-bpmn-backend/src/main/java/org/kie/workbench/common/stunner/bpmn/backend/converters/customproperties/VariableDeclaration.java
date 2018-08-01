/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties;

import java.util.Objects;

import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Property;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Ids;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

public class VariableDeclaration {

    private final ItemDefinition typeDeclaration;
    private final Property typedIdentifier;
    private String identifier;
    private String type = "";

    public VariableDeclaration(String identifier, String type) {
        this.identifier = identifier;
        this.type = type;

        this.typeDeclaration = bpmn2.createItemDefinition();
        this.typeDeclaration.setId(Ids.item(identifier));
        this.typeDeclaration.setStructureRef(type);

        this.typedIdentifier = bpmn2.createProperty();
        this.typedIdentifier.setId(Ids.typedIdentifier("GLOBAL", identifier));
        this.typedIdentifier.setName(identifier);
        this.typedIdentifier.setItemSubjectRef(typeDeclaration);

    }

    public static VariableDeclaration fromString(String encoded) {
        String[] split = encoded.split(":");
        String identifier = split[0];
        String type = (split.length == 2) ? split[1] : "";
        if (identifier.isEmpty()) {
            throw new IllegalArgumentException("Variable identifier cannot be empty. Given: '" + encoded + "'");
        }
        return new VariableDeclaration(identifier, type);
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ItemDefinition getTypeDeclaration() {
        return typeDeclaration;
    }

    public Property getTypedIdentifier() {
        return typedIdentifier;
    }

    @Override
    public String toString() {
        if (type == null || type.isEmpty()) {
            return identifier;
        } else {
            return identifier + ":" + type;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VariableDeclaration that = (VariableDeclaration) o;
        return Objects.equals(identifier, that.identifier) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, type);
    }
}
