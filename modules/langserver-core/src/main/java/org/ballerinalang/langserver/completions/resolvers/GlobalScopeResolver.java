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

package org.ballerinalang.langserver.completions.resolvers;

import org.antlr.v4.runtime.ParserRuleContext;
import org.ballerinalang.langserver.completions.SuggestionsFilterDataModel;
import org.ballerinalang.langserver.completions.SymbolInfo;
import org.ballerinalang.langserver.completions.consts.CompletionItemResolver;
import org.eclipse.lsp4j.CompletionItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Completion item resolver for the global scope.
 */
public class GlobalScopeResolver extends AbstractItemResolver {

    @Override
    public ArrayList<CompletionItem> resolveItems(SuggestionsFilterDataModel dataModel) {

        ArrayList<CompletionItem> completionItems = new ArrayList<>();


        ParserRuleContext parserRuleContext = dataModel.getParserRuleContext();

        if (parserRuleContext == null) {
            // If the parser rule context is null we don't have any errors. In this case we add the types
            List<SymbolInfo> bTypeSymbolInfo = dataModel.getVisibleSymbols()
                    .stream()
                    .filter(symbolInfo -> symbolInfo.getScopeEntry().symbol.type != null)
                    .collect(Collectors.toList());
            this.populateCompletionItemList(bTypeSymbolInfo, completionItems);
        } else {
            return CompletionItemResolver.getResolverByClass(parserRuleContext.getClass()).resolveItems(dataModel);
        }

        return completionItems;
    }
}
