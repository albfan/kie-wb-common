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

package org.kie.workbench.common.screens.datamodeller.backend.server;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceDescriptorEditorContent;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceDescriptorModel;
import org.kie.workbench.common.screens.datamodeller.service.PersistenceDescriptorEditorService;
import org.kie.workbench.common.screens.datamodeller.service.PersistenceDescriptorService;
import org.kie.workbench.common.services.backend.service.KieService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.editor.commons.backend.service.SaveAndRenameServiceImpl;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.io.IOService;

@Service
@ApplicationScoped
public class PersistenceDescriptorEditorServiceImpl
        extends KieService<PersistenceDescriptorEditorContent>
        implements PersistenceDescriptorEditorService {

    private final IOService ioService;

    private final RenameService renameService;

    private final PersistenceDescriptorService descriptorService;

    private final SaveAndRenameServiceImpl<PersistenceDescriptorEditorContent, Metadata> saveAndRenameService;

    @Inject
    public PersistenceDescriptorEditorServiceImpl(final @Named("ioStrategy") IOService ioService,
                                                  final PersistenceDescriptorService descriptorService,
                                                  final RenameService renameService,
                                                  final SaveAndRenameServiceImpl<PersistenceDescriptorEditorContent, Metadata> saveAndRenameService) {
        this.ioService = ioService;
        this.descriptorService = descriptorService;
        this.renameService = renameService;
        this.saveAndRenameService = saveAndRenameService;
    }

    @PostConstruct
    public void init() {
        saveAndRenameService.init(this);
    }

    @Override
    public PersistenceDescriptorEditorContent loadContent(Path path, boolean createIfNotExists) {

        PersistenceDescriptorEditorContent content;
        if (createIfNotExists) {
            //was called manually form the project editor
            Pair<Path, Boolean> createIfNotExistsResult = createIfNotExists(path);
            content = loadContent(createIfNotExistsResult.getK1());
            content.setPath(createIfNotExistsResult.getK1());
            content.setCreated(createIfNotExistsResult.getK2());
        } else {
            content = loadContent(path);
            content.setPath(path);
            content.setCreated(false);
        }
        return content;
    }

    @Override
    protected PersistenceDescriptorEditorContent constructContent(Path path, Overview overview) {

        PersistenceDescriptorEditorContent content = new PersistenceDescriptorEditorContent();
        PersistenceDescriptorModel descriptorModel = descriptorService.load(path);
        content.setDescriptorModel(descriptorModel);
        content.setOverview(overview);
        content.setSource(ioService.readAllString(Paths.convert(path)));

        return content;
    }

    @Override
    public Path save(Path path, PersistenceDescriptorEditorContent content, Metadata metadata, String comment) {

        if (content != null && content.getDescriptorModel() != null) {
            descriptorService.save(path, content.getDescriptorModel(), metadata, comment);
        }
        return path;
    }

    public Pair<Path, Boolean> createIfNotExists(Path path) {
        if (ioService.notExists(Paths.convert(path))) {
            PersistenceDescriptorModel descriptorModel = descriptorService.createModuleDefaultDescriptor(path);
            Path createdPath = descriptorService.save(path, descriptorModel, null, "Default persistence descriptor generated by system");
            return new Pair<Path, Boolean>(createdPath, true);
        } else {
            //workaround to add hasVersionSupport property to the received path, in case the FS supports versioning.
            //When the path was created manually it doesn't have the property.
            return new Pair<Path, Boolean>(Paths.normalizePath(path), false);
        }
    }

    @Override
    public Path saveAndRename(final Path path,
                              final String newFileName,
                              final Metadata metadata,
                              final PersistenceDescriptorEditorContent content,
                              final String comment) {
        return saveAndRenameService.saveAndRename(path, newFileName, metadata, content, comment);
    }

    @Override
    public Path rename(final Path path,
                       final String newName,
                       final String comment) {
        return renameService.rename(path, newName, comment);
    }
}
