/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client.widgets.jpadomain.properties;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.properties.BasePopupField;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.properties.BasePopupPropertyEditorWidget;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;

@Dependent
public class SequenceGeneratorField extends BasePopupField {

    public static final String NOT_CONFIGURED_LABEL = Constants.INSTANCE.persistence_domain_relationship_sequence_generator_dialog_not_configured_label();

    private SequenceGeneratorEditorWidget widget;

    @Inject
    public SequenceGeneratorField(SequenceGeneratorEditorWidget widget) {
        this.widget = widget;
    }

    @Override
    protected BasePopupPropertyEditorWidget createPopupPropertyEditor(PropertyEditorFieldInfo property) {
        widget.setProperty(property);
        return widget;
    }
}
