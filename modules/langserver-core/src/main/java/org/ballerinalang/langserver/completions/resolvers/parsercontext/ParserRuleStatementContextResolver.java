/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/

package org.ballerinalang.langserver.completions.resolvers.parsercontext;

import org.ballerinalang.langserver.completions.SuggestionsFilterDataModel;
import org.ballerinalang.langserver.completions.SymbolInfo;
import org.ballerinalang.langserver.completions.consts.ItemResolverConstants;
import org.ballerinalang.langserver.completions.consts.Priority;
import org.ballerinalang.langserver.completions.consts.Snippet;
import org.ballerinalang.langserver.completions.resolvers.AbstractItemResolver;
import org.ballerinalang.langserver.completions.util.filters.PackageActionAndFunctionFilter;
import org.ballerinalang.langserver.completions.util.filters.StatementTemplateFilter;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.InsertTextFormat;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Parser rule based statement context resolver.
 */
public class ParserRuleStatementContextResolver extends AbstractItemResolver {
    @Override
    public ArrayList<CompletionItem> resolveItems(SuggestionsFilterDataModel dataModel) {

        HashMap<String, String> prioritiesMap = new HashMap<>();
        ArrayList<CompletionItem> completionItems = new ArrayList<>();

        // Here we specifically need to check whether the statement is function invocation,
        // action invocation or worker invocation
        if (isActionOrFunctionInvocationStatement(dataModel)) {
            PackageActionAndFunctionFilter actionAndFunctionFilter = new PackageActionAndFunctionFilter();

            // Get the action and function list
            ArrayList<SymbolInfo> actionFunctionList = new ArrayList<>();
            actionFunctionList.addAll(actionAndFunctionFilter.filterItems(dataModel));

            // Populate the completion items
            this.populateCompletionItemList(actionFunctionList, completionItems);

            // Set the sorting priorities
            prioritiesMap.put(ItemResolverConstants.FUNCTION_TYPE, Priority.PRIORITY7.name());
            prioritiesMap.put(ItemResolverConstants.ACTION_TYPE, Priority.PRIORITY6.name());
            this.assignItemPriorities(prioritiesMap, completionItems);

            return completionItems;
        } else {
            populateCompletionItemList(dataModel.getVisibleSymbols(), completionItems);
            StatementTemplateFilter statementTemplateFilter = new StatementTemplateFilter();
            // Add the statement templates
            completionItems.addAll(statementTemplateFilter.filterItems(dataModel));
            this.populateBasicTypes(completionItems, dataModel.getSymbolTable());

            CompletionItem xmlns = new CompletionItem();
            xmlns.setLabel(ItemResolverConstants.XMLNS);
            xmlns.setInsertText(Snippet.NAMESPACE_DECLARATION.toString());
            xmlns.setInsertTextFormat(InsertTextFormat.Snippet);
            xmlns.setDetail(ItemResolverConstants.SNIPPET_TYPE);
            xmlns.setSortText(Priority.PRIORITY7.name());
            completionItems.add(xmlns);

            CompletionItem workerItem = new CompletionItem();
            workerItem.setLabel(ItemResolverConstants.WORKER);
            workerItem.setInsertText(Snippet.WORKER.toString());
            workerItem.setInsertTextFormat(InsertTextFormat.Snippet);
            workerItem.setDetail(ItemResolverConstants.WORKER_TYPE);
            workerItem.setSortText(Priority.PRIORITY6.name());
            completionItems.add(workerItem);

            CompletionItem xmlAttribute = new CompletionItem();
            xmlAttribute.setInsertText(Snippet.XML_ATTRIBUTE_REFERENCE.toString());
            xmlAttribute.setInsertTextFormat(InsertTextFormat.Snippet);
            xmlAttribute.setLabel("@");
            xmlAttribute.setDetail("xmlAttribute");
            xmlAttribute.setSortText(Priority.PRIORITY6.name());
            completionItems.add(xmlAttribute);

            // Add the var keyword
            CompletionItem varKeyword = new CompletionItem();
            varKeyword.setInsertText("var ");
            varKeyword.setLabel("var");
            varKeyword.setDetail(ItemResolverConstants.KEYWORD_TYPE);
            varKeyword.setSortText(Priority.PRIORITY6.name());
            completionItems.add(varKeyword);

            prioritiesMap.put(ItemResolverConstants.PACKAGE_TYPE, Priority.PRIORITY6.name());
            prioritiesMap.put(ItemResolverConstants.STATEMENT_TYPE, Priority.PRIORITY5.name());
            this.assignItemPriorities(prioritiesMap, completionItems);

            return completionItems;
        }
    }
}

