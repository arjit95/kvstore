package commands

import (
	"github.com/arjit95/kvstore/go/cmd/kvctl/factory"
	"github.com/arjit95/kvstore/go/pkg/client"
	"github.com/spf13/cobra"
)

func CreatePutCommand(f *factory.Factory) *cobra.Command {
	return &cobra.Command{
		Use:                   "put",
		Short:                 "Adds a new key to server",
		Args:                  cobra.ExactArgs(2),
		DisableFlagsInUseLine: true,
		RunE: func(cmd *cobra.Command, args []string) error {
			entry := client.Entry{
				Key:   args[0],
				Value: []byte(args[1]),
			}

			for _, client := range f.Clients {
				err := client.Put(entry)
				if err != nil {
					return err
				}
			}

			cmd.Printf("Successfully inserted key %s\n", args[0])

			return nil
		},
	}
}
