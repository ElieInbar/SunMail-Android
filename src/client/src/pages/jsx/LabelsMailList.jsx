const DEFAULT_LABELS = ["starred",  "important", "sent", "drafts", "trash", "all"];

function LabelsMailList({ mailLabelObjects, user, labelsUser, handleRemoveLabel }) {

    return (
        < div className="mail-labels" >
            {mailLabelObjects.length
                ? mailLabelObjects.map(lab => {
                    const labelObj = typeof lab === "object" ? lab : labelsUser.find(x => x.id === lab);
                    if (!labelObj) return null;
                    if (DEFAULT_LABELS.includes(labelObj.name)) return null;

                    return (
                        <div className="mail-label-chip" key={labelObj.id}>
                            <span className="chip-label-text">{labelObj.name}</span>
                            <button
                                className="chip-remove-btn"
                                onClick={async (e) => {
                                    e.stopPropagation();
                                    await handleRemoveLabel(labelObj.id);
                                }}
                                title="Remove this label"
                                tabIndex={0}
                            >
                                &times;
                            </button>
                        </div>
                    );
                })
                : <span className="mail-label-chip no-label">No Labels</span>}
        </div >
    )
}

export default LabelsMailList;