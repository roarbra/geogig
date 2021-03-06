/* Copyright (c) 2017 Boundless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/org/documents/edl-v10.html
 *
 * Contributors:
 * Johnathan Garrett (Prominent Edge) - initial implementation
 */
package org.locationtech.geogig.cli.porcelain.index;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.locationtech.geogig.cli.AbstractCommand;
import org.locationtech.geogig.cli.CLICommand;
import org.locationtech.geogig.cli.CommandFailedException;
import org.locationtech.geogig.cli.Console;
import org.locationtech.geogig.cli.GeogigCLI;
import org.locationtech.geogig.cli.InvalidParameterException;
import org.locationtech.geogig.cli.annotation.RequiresRepository;
import org.locationtech.geogig.repository.IndexInfo;
import org.locationtech.geogig.repository.Repository;

import com.google.common.collect.Iterators;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@RequiresRepository(true)
@Command(name = "list", aliases = "l", description = "List all indexes in the repository.")
public class ListIndexes extends AbstractCommand implements CLICommand {

    @Option(names = "--tree", description = "Name of the feature tree to list indexes for.")
    private String treeName;

    protected @Override void runInternal(GeogigCLI cli)
            throws InvalidParameterException, CommandFailedException, IOException {

        Repository repo = cli.getGeogig().getRepository();

        List<IndexInfo> indexInfos;
        if (treeName != null) {
            indexInfos = repo.context().indexDatabase().getIndexInfos(treeName);
        } else {
            indexInfos = repo.context().indexDatabase().getIndexInfos();
        }

        Function<IndexInfo, CharSequence> printFunctor = new Function<IndexInfo, CharSequence>() {

            public @Override CharSequence apply(IndexInfo input) {
                StringBuilder sb = new StringBuilder();
                sb.append("Index [").append(input.getId()).append("]\n");
                sb.append("  ").append("Feature Type Tree:\t").append(input.getTreeName())
                        .append("\n");
                sb.append("  ").append("Attribute:\t\t").append(input.getAttributeName())
                        .append("\n");
                sb.append("  ").append("Index Type:\t\t").append(input.getIndexType()).append("\n");
                Map<String, Object> metadata = input.getMetadata();
                if (metadata.containsKey(IndexInfo.MD_QUAD_MAX_BOUNDS)) {
                    sb.append("  ").append("Quad Tree Max Bounds:\t")
                            .append(metadata.get(IndexInfo.MD_QUAD_MAX_BOUNDS)).append("\n");
                }
                if (metadata.containsKey(IndexInfo.FEATURE_ATTRIBUTES_EXTRA_DATA)) {
                    String[] extraAttributes = (String[]) metadata
                            .get(IndexInfo.FEATURE_ATTRIBUTES_EXTRA_DATA);
                    sb.append("  ").append("Extra Attributes:\t")
                            .append(Arrays.toString(extraAttributes)).append("\n");
                }
                return sb;
            }
        };

        Iterator<CharSequence> lines = Iterators.transform(indexInfos.iterator(),
                printFunctor::apply);

        Console console = cli.getConsole();
        while (lines.hasNext()) {
            console.println(lines.next());
        }
        console.flush();

    }
}
