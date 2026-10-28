// Turns each [data-choice-group="name"] holding .choice-tag buttons into a multi-select. The ticked values are posted
// as hidden inputs named "name", kept in the matching [data-choice-inputs="name"] (which the page fills with the saved ones).
// Buttons rather than spans, so the tags can be reached and ticked with the keyboard.
document.querySelectorAll('[data-choice-group]').forEach(group => {
    const name = group.dataset.choiceGroup;
    const inputs = document.querySelector(`[data-choice-inputs="${name}"]`);
    const selected = new Set([...inputs.querySelectorAll('input')].map(input => input.value));

    function writeInputs() {
        inputs.innerHTML = '';
        selected.forEach(value => {
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = name;
            input.value = value;
            inputs.appendChild(input);
        });
    }

    group.querySelectorAll('.choice-tag').forEach(tag => {
        tag.setAttribute('aria-pressed', selected.has(tag.dataset.value));
        tag.addEventListener('click', () => {
            const value = tag.dataset.value;
            if (selected.has(value)) selected.delete(value); else selected.add(value);
            tag.setAttribute('aria-pressed', selected.has(value));
            writeInputs();
        });
    });
});
