const fs = require('fs');
const path = require('path');

function walk(dir) {
    let results = [];
    if (!fs.existsSync(dir)) return results;
    const list = fs.readdirSync(dir);
    list.forEach(file => {
        file = path.join(dir, file);
        const stat = fs.statSync(file);
        if (stat && stat.isDirectory()) {
            results = results.concat(walk(file));
        } else {
            if (file.endsWith('.vue') || file.endsWith('.ts')) {
                results.push(file);
            }
        }
    });
    return results;
}

const files = walk(path.join(__dirname, 'frontend/src'));

let changedCount = 0;

files.forEach(file => {
    let content = fs.readFileSync(file, 'utf8');
    let original = content;

    // Fix array declarations: const headers = ref([ { text: '...', value: '...' } ])
    content = content.replace(/((?:const|let|var)\s+\w*[Hh]eaders\s*[:=][\s\S]*?(?:\[))([\s\S]*?)(\](?:\s*\))?\s*;)/gm, (match, prefix, inner, suffix) => {
        let newInner = inner.replace(/\bvalue(\s*:)/g, 'key$1');
        newInner = newInner.replace(/\btext(\s*:)/g, 'title$1');
        return prefix + newInner + suffix;
    });

    // Fix generic inline header array objects `{ text: '...', value: '...' }` that might have been missed
    const oneLinerRegex = /(\{\s*(?:title|text)\s*:.*?,\s*)(value)(\s*:.*?\})/g;
    content = content.replace(oneLinerRegex, '$1key$3');

    const oneLinerRegex2 = /(\{\s*)(value)(\s*:.*?,\s*(?:title|text)\s*:.*?\})/g;
    content = content.replace(oneLinerRegex2, '$1key$3');

    // Make sure we change 'text:' to 'title:' as well in one liners
    const oneLinerRegexTextTitle = /(\{\s*)(text)(\s*:.*?,\s*key\s*:.*?\})/g;
    content = content.replace(oneLinerRegexTextTitle, '$1title$3');

    const oneLinerRegexTextTitle2 = /(\{\s*key\s*:.*?,\s*)(text)(\s*:.*?\})/g;
    content = content.replace(oneLinerRegexTextTitle2, '$1title$3');

    if (content !== original) {
        fs.writeFileSync(file, content);
        changedCount++;
        console.log(`Updated headers in ${file.split('frontend/src/')[1]}`);
    }
});

console.log(`Updated ${changedCount} files.`);
